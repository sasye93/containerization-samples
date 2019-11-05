/*package thesis.samples.eval.gateway.api

// todo this requires node.js, make clear + NOT WORKING

import akka.actor.ActorSystem
import loci.contexts.Pooled.Implicits.global
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpCharsets.`UTF-8`
import akka.http.scaladsl.model.MediaTypes.`application/xhtml+xml`
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, MethodRejection, RejectionHandler, Route}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Tcp.ServerBinding
import loci._
import loci.transmitter.rescala._
import loci.serializer.upickle._
import loci.communicator.tcp._
import loci.container._
import rescala.default._
import thesis.samples.eval.{Author, Input, Pipeline}

import util.control.Breaks._
import scala.io.StdIn
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, duration}

@multitier object ConsumerApi extends thesis.samples.eval.GatewayApi {
  @peer type Gateway <: { type Tie <: Optional[Pipeline.Input] }

  //private def getTweetsByHashtag(tags : String) : List[String] on Gateway = on[Gateway]{ implicit! => (remote call Pipeline.getTweetsByHashtag(tags)).asLocal }

  def asyncCall[T](f : Option[Future[T]]): String = f match {
    case Some(f: Future[T]) => Await.result(f, Duration(5L, duration.SECONDS)).toString
    case None => "Error: Could not retrieve data from Service."
  }
  //val clientTime : Signal[Option[Long]] on Gateway = placed{ MultitierApi.clientTime.asLocal }

  on[Gateway] { implicit ! =>

    //Pipeline.tweetStream.asLocal
    //val display = Signal { (new SimpleDateFormat("hh:mm:ss")) format new Date(clientTime.apply) }
    /**
    clientTime.changed observe { x =>
      print(x.getOrElse(0L))
      println(clientTime.now.getOrElse(0L))
      //timer.time = MultitierApi.t.asLocal
    }
     */

    ////MultitierApi.clientTime.asLocal.changed observe println
    //MultitierApi.getTime.asLocal.changed observe print

  }
  def main(): Unit on Gateway = on[Gateway]{
    println(s"1")
    implicit val system : ActorSystem = ActorSystem("DFKSDFESWFIMNEWFISW")
    println(s"2")
    implicit val materializer : ActorMaterializer = ActorMaterializer()
    println(s"23")
    implicit def exceptionHandler: ExceptionHandler =
      ExceptionHandler {
        case err : Throwable =>
          extractUri { uri =>
            println(s"Failure in request to $uri: ${ err.getMessage }")
            complete(HttpResponse(status = StatusCodes.InternalServerError, entity = "Something went wrong."))
          }
      }
    println(s"3")
    implicit def rejectionHandler : RejectionHandler =
      RejectionHandler.newBuilder()
        .handleAll[MethodRejection] { implicit! => complete((StatusCodes.MethodNotAllowed, "This API Gateway only supports GET requests.")) }
        .handleNotFound { complete(HttpResponse(StatusCodes.NotFound, entity = "Route not found. Maybe your API type doesn't support this action?")) }
        .result()

    println(s"4")
    val consumerRoutes =
      get {
        pathSingleSlash{
          complete("okay")
        } ~
          path("test"){
            complete("whas up")
          } ~
          path("tweet") {
            parameters('author, 'content) { (key, value) => {
              println(key + ":" + value)
              //asyncCall ((remote call Pipeline.tweet(key, value)).asLocal)
              complete("whas up")
            }
            }
          }
      }
    println(s"5")
    val bindingFutures = List{
      Http().bindAndHandle(Route.handlerFlow(consumerRoutes), Tools.publicIp, 8424)
    }
    println(s"6")

    bindingFutures foreach { b => b.failed foreach { _ => shutdown(b) } }
    println(s"API Gateway - business API - online at http://localhost:8421/")
    while(scala.io.Source.stdin.getLines.next() != "quit"){}

    def shutdown(b : Future[Http.ServerBinding]) = {
      println("shutting down...")
      b.flatMap(_.unbind())
      b.onComplete(_ => system.terminate())
      Await.result(system.whenTerminated, Duration.Inf)
    }
    bindingFutures foreach { b => shutdown(b) } // cleanup on shutdown
    materializer.shutdown
  }
}
@gateway(
  """{
    |  "ports": "8424"
    |}""")
object APIGateway extends App {
  (loci.multitier start new Instance[ConsumerApi.Gateway](
    connect[Pipeline.Input] { TCP(Tools.resolveIp(Input), 1098) }
  ))
}*/