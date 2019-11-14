/**
  * This is the traditional, uncontainerized ScalaLoci version of the pipeline evaluation example from chapter 8.
  * this needs a running mongoDB on localhost:27017 (one might simply run a docker mongo container).
  * remember to mask special characters in your http request, like space = %20, hashtag = %23.
  * You can use cURL to post request, http://localhost:8424.
  */

package thesis.samples.eval
package classic

import java.util.{UUID,Calendar}

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import loci.transmitter.IdenticallyTransmittable
import loci.transmitter.rescala._
import loci.communicator.tcp._
import loci.serializer.upickle._
import org.mongodb.scala.bson.{BsonArray, BsonString}
import rescala.default._
import upickle.default._
import org.mongodb.scala.model.Filters._
import akka.http.scaladsl.server.Directives._
import thesis.samples.common.Db
import loci.contexts.Pooled.Implicits.global
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, MethodRejection, RejectionHandler, Route}
import loci._
import loci.communicator.tcp._
import org.mongodb.scala.{Completed, Observer}
import scala.collection.mutable

import java.security.MessageDigest
import java.math.BigInteger

import scala.concurrent.{Await, Future, Promise, duration}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Random, Success}

case class Author(name: String)

object Author {
  implicit val authorTransmittable: IdenticallyTransmittable[Author] = IdenticallyTransmittable()
  implicit val authorSerializer: ReadWriter[Author] = macroRW[Author]
}

case class Tweet(line : String, tags: Set[String], author: Author, confirmID : UUID, sha256 : String = null) {
  def hasHashtag(tag: String) = tags contains tag
}

object Tweet {
  implicit val tweetTransmittable: IdenticallyTransmittable[Tweet] = IdenticallyTransmittable()
  implicit val tweetSerializer: ReadWriter[Tweet] = macroRW[Tweet]
}
package object db{
  lazy val db : Db = new Db("mongodb://127.0.0.1:27017", "tweets")//todo on arch, throws recursive error if inside containerize
}
package object sha256{
  def generateSha256(message : String) : String =
    String.format("%032x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(message.getBytes("UTF-8"))))
}
@multitier object Pipeline{
  @peer type Peer

  @peer type Input <: Peer { type Tie <: Single[Tagger] with Single[Archiver] }
  @peer type Tagger <: Peer { type Tie <: Single[Filter] with Multiple[Input] }
  @peer type Filter <: Peer { type Tie <: Multiple[Tagger] with Single[Hasher] with Single[Archiver] }
  @peer type Hasher <: Peer { type Tie <: Multiple[Filter] with Multiple[Output] with Single[Archiver] }
  @peer type Archiver <: Peer { type Tie <: Multiple[Hasher] with Multiple[Input] with Multiple[Filter] }
  @peer type Output <: Peer { type Tie <: Single[Hasher] }

  private def asyncCall[T](f : Option[Future[T]]): String = f match {
    case Some(f: Future[T]) => Await.result(f, Duration(5L, duration.SECONDS)).toString
    case None => "Error: Could not retrieve data from Service."
  }
  val filteredTags : Set[String] = Set(
    "reactive",
    "multitier",
    "microservice",
    "distributed",
    "observer"
  )

  val tweetStream: Evt[(Author, String, UUID)] on Input = Evt[(Author, String, UUID)]

  val confirmationStream: Evt[(UUID, String)] on Archiver = Evt[(UUID, String)]
  def confirm(tweet : (UUID, String)) : Unit on Archiver = placed{ confirmationStream.fire(tweet) }

  def getHashtags(line : String) : Set[String] = "#(([a-zA-Z0-9]|[^#\\s,.?!])+)".r.findAllIn(line).matchData.map(_.group(1)).toSet
  def tweet(author : String, content : String) : UUID on Input = placed{
    val confirmID : UUID = UUID.randomUUID
    tweetStream.fire((Author(author), content, confirmID))
    confirmID
  }

  val tagged : Event[Tweet] on Tagger = on[Tagger] { implicit! =>
    tweetStream.asLocalFromAllSeq collect{
      case (remote, tweet) => Tweet(tweet._2, getHashtags(tweet._2), tweet._1, tweet._3)
    }
  }

  val filtered : Event[Tweet] on Filter = on[Filter] { implicit! =>
    (tagged.asLocalFromAllSeq collect{
      case (_, tweet : Tweet) if(tweet.line.length <= 140 && tweet.tags.intersect(filteredTags).nonEmpty) => Some(tweet)
      case (_, tweet : Tweet) => (remote call confirm((tweet.confirmID, s"Tweet filtered out: ${ tweet.line }"))); None
    }).flatten
  }

  val hashed: Event[Tweet] on Hasher = on[Hasher]{ implicit! =>
    filtered.asLocalFromAllSeq collect{
      case (remote, tweet) => tweet.copy(sha256 = sha256.generateSha256(tweet.line))
    }
  }

  on[Archiver]{ implicit! =>
    hashed.asLocalFromAllSeq.observe{case (_, t : Tweet) => {
      val seq = Seq() :+ ("author" -> BsonString(t.author.name)) :+ ("tags" -> BsonArray(t.tags.map(BsonString(_)))) :+ ("tweet" -> BsonString(t.line)) :+ ("hash" -> BsonString(t.sha256))
      db.db.insertDocumentObserved("tweets", seq, (new Observer[Completed]{
        override def onNext(result: Completed): Unit = {}
        override def onError(e: Throwable): Unit = confirm((t.confirmID, "Error while trying to archive your tweet. Sorry!"))
        override def onComplete(): Unit = confirm((t.confirmID, s"Successfully tweeted: ${ t.line }"))
      }))
    }
    }
  }

  def getTweetsByHashtag(tag : String) : Unit on Archiver = placed{
    db.db.getDocuments("tweets", all("tags", tag), null) //todo
  }

  def main(): Unit on Peer =
    (on[Output] {
      hashed.asLocal observe { tweet =>
        //val tags : String = folded1.asLocal.map(_.filterKeys(filteredTags.contains).map(tag => s"#${ tag._1 }: ${ tag._2 }").mkString(", ")).now
        println(s"${ tweet.author.name } says: ${ tweet.line }")
      }
    }) and
      (on[Input] {
        println(s"HTTP Server Starting up...")
        implicit val system : ActorSystem = ActorSystem("default")
        implicit val materializer : ActorMaterializer = ActorMaterializer()
        implicit def exceptionHandler: ExceptionHandler =
          ExceptionHandler {
            case err : Throwable =>
              extractUri { uri =>
                println(s"Failure in request to $uri: ${ err.getMessage }")
                complete(HttpResponse(status = StatusCodes.InternalServerError, entity = "Something went wrong."))
              }
          }
        implicit def rejectionHandler : RejectionHandler =
          RejectionHandler.newBuilder()
            .handleAll[MethodRejection] { implicit! => complete((StatusCodes.MethodNotAllowed, "This API Gateway only supports GET requests.")) }
            .handleNotFound { complete(HttpResponse(StatusCodes.NotFound, entity = "Route not found. Maybe your API type doesn't support this action?")) }
            .result()

        val consumerRoutes =
          get {
            pathSingleSlash{
              complete("Server is up. GET /benchmark?iterates=X or /tweet?author=A&content=C")
            } ~
              path("tweet") {
                parameters('author, 'content) { (author, content) =>
                  //%20 = space, %23 = #
                  val confirmID = tweet(author, content)
                  val p = Promise[String]()
                  confirmationStream.asLocal.filter(_._1 == confirmID).map(_._2).observe(p.success)
                  onComplete(p.future) {
                    case Success(res) => complete(res.toString)
                    case Failure(res) => complete((StatusCodes.InternalServerError, s"An error occurred: ${res.getMessage}"))
                  }
                }
              } ~
              path("benchmark") {
                withRequestTimeout(Duration(1L, duration.MINUTES), _ => HttpResponse(StatusCodes.EnhanceYourCalm, entity = "Unable to serve response within time limit. Reduce iterates param.")) {
                  parameters('iterates.as[Int]) { (iterates) =>
                    val confirmIDs: mutable.Set[UUID] = mutable.Set[UUID]()
                    val tweets: List[(String, String)] = List(
                      ("Charles", "Hi there, #multitier #programming is really cool!"),
                      ("Simon", "This is a random tweet. #totally #random #filter #this #out"),
                      ("Clemens", "You might want to checkout the new #multitier #containerize extension."),
                      ("Simon", "This is just another random tweet, but this time, #cooler."),
                      ("Sophie", "What do you use, #reactive #observer #events or #callbacks ?"),
                      ("Jana", "Have you seen the new Star Wars movie?"),
                      ("Jake", "#event based #programming makes developing #distributed systems easier."),
                      ("Anne", "Just wanted to say hello!"),
                      ("Michael", "If you want to be cool, you go #reactive , #event is out!")
                    )
                    var its = 0
                    val p = Promise[String]()
                    val timeOut = System.nanoTime()
                    confirmationStream.asLocal.latest.map(_._1).observe(uuid => {
                      /*if (confirmIDs remove uuid)*/ its += 1
                      if (its >= iterates)
                        p.trySuccess("")
                    }, println, (_ : UUID) => (its >= iterates || ((System.nanoTime()-timeOut) / 1000000000) > 60))
                    val t0 = System.nanoTime()
                    val response = onComplete(p.future) {
                      {
                        case Success(_) => complete(s"${(System.nanoTime() - t0) / 1000000}")
                        case Failure(res) => complete((StatusCodes.InternalServerError, s"An error occurred: ${res.getMessage}"))
                      }
                    }
                    val randomGen = new Random(iterates * Calendar.getInstance().get(Calendar.SECOND))
                    for (i <- 1 to iterates) {
                      val t = tweets(randomGen.nextInt(tweets.size))
                      confirmIDs add tweet(t._1, t._2)
                    }
                    response
                  }
                }
              }
          }
        val bindingFutures = List{
          Http().bindAndHandle(Route.handlerFlow(consumerRoutes), "127.0.0.1", 8424)
        }
        println("HTTP Server is up. To tweet, make a GET request with 'author' and 'content' parameters on :8424.")

        bindingFutures foreach { b => b.failed foreach { _ => shutdown(b) } }
        while(scala.io.Source.stdin.getLines.next() != "quit"){}

        def shutdown(b : Future[Http.ServerBinding]) = {
          println("Shutting down...")
          b.flatMap(_.unbind())
          b.onComplete(_ => system.terminate())
          Await.result(system.whenTerminated, Duration.Inf)
        }
        bindingFutures foreach { b => shutdown(b) } // cleanup on shutdown
        materializer.shutdown
        multitier.terminate
      })
}


object startServices extends App{
  multitier start new Instance[Pipeline.Archiver](
    listen[Pipeline.Hasher] {
      TCP(8161, "localhost")
    } and
      listen[Pipeline.Input] {
        TCP(8162, "localhost")
      } and
      listen[Pipeline.Filter] {
        TCP(8163, "localhost")
      })
  multitier start new Instance[Pipeline.Hasher](
    listen[Pipeline.Output] {
      TCP(8164, "localhost")
    } and
      connect[Pipeline.Filter] {
        TCP(8165, "localhost").firstConnection
      } and
      connect[Pipeline.Archiver] {
        TCP("localhost", 8161)
      })
  multitier start new Instance[Pipeline.Filter](
    listen[Pipeline.Tagger] {
      TCP(8166, "localhost")
    } and
      connect[Pipeline.Hasher] {
        TCP("localhost", 8165)
      } and
      connect[Pipeline.Archiver] {
        TCP("localhost", 8163)
      })
  multitier start new Instance[Pipeline.Tagger](
    listen[Pipeline.Input] {
      TCP(8169, "localhost")
    } and
      connect[Pipeline.Filter] {
        TCP("localhost", 8166)
      })
  multitier start new Instance[Pipeline.Output](
    connect[Pipeline.Hasher] {
      TCP("localhost", 8164)
    })
  multitier start new Instance[Pipeline.Input](
    connect[Pipeline.Tagger] {
      TCP("localhost", 8169)
    } and
      connect[Pipeline.Archiver] {
        TCP("localhost", 8162)
      })
}