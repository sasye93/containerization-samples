package thesis.samples
package timeservice

import loci._
import loci.transmitter.rescala._
import loci.serializer.upickle._
import loci.communicator.tcp._
import rescala.default._
import java.util.{Calendar, Date}
import java.text.SimpleDateFormat

import loci.container.Tools._
import loci.container._

/**
  * Public Api
  */
@multitier protected trait Api{
  /**
    * Services
    */
  @peer type Peer
  @peer type Client <: Peer
  @peer type Server <: Peer

  lazy final val version = 1.0
  /**
    * Api provided by Server Service
    */
  val time : Var[Long] on Server
}
/**
  * Client implementation
  */
@multitier protected[thesis] trait ClientImpl extends Api {
  @peer type Client <: Peer { type Tie <: Single[Server] }

  on[Client] { implicit! =>
    val display = Signal { (new SimpleDateFormat("hh:mm:ss")) format new Date(time.asLocal()) }
    display.changed observe println
  }
}
/**
  * Service implementation
  */
object db{
  def db() : Unit = {

    import org.mongodb.scala._
    import bson._

    // Use a Connection String
    val mongoClient: MongoClient = MongoClient(Tools.globalDbIp(MultitierApi))
    val mongoClient2: MongoClient = MongoClient(Tools.localDbIp(Service))

    val database: MongoDatabase = mongoClient.getDatabase("mydb")
    val database2: MongoDatabase = mongoClient2.getDatabase("mydb")
    //database.createCollection("global")
    database2.createCollection("local")
    val collection = database.getCollection("global")
    val collection2 = database2.getCollection("local")
    val doc: Document = bson.Document("_id" -> 0, "name" -> "MongoDB", "type" -> "database",
      "count" -> 1, "info" -> bson.Document("x" -> 203, "y" -> 102))
    collection.insertOne(doc).subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = println("Inserted")
      override def onError(e: Throwable): Unit = println("Failed")
      override def onComplete(): Unit = println("Completed")
    })
    collection2.insertOne(doc).subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = println("Inserted")
      override def onError(e: Throwable): Unit = println("Failed")
      override def onComplete(): Unit = println("Completed")
    })
  }
}
@multitier protected[thesis] trait ServerImpl extends Api{
  @peer type Server <: Peer { type Tie <: Multiple[Client] }
  val time: Var[Long] on Server = on[Server] { implicit! => Var(0L) }

  def main() : Unit on Server = placed{ implicit! =>
    db.db()
    while (true) {
      time set Calendar.getInstance.getTimeInMillis
      Thread sleep 1000
    }
  }
}
@multitier /*@containerize(
  """{
    |  "globalDb": "mongo",
    |  "stateful": "true"
    }"""
)*/ object MultitierApi extends ServerImpl with ClientImpl

@service(
  """{
    |  "type": "service",
    |  "localDb": "mongo",
    |  "replicas": 2,
    |  "attachable": "false"
    |}"""
)
object Service extends App {
  loci.multitier start new Instance[MultitierApi.Server](
    listen[MultitierApi.Client] { TCP(4577, Tools.publicIp) }
  )
}

@service(
  """{
    |  "type": "service",
    |  "replicas": 3,
    |  "description": "this is a test service! WOOOOOOOOOOW ",
    |  "ports": "5,423,412,64363"
    |}"""
)
object Client extends App {
  loci.multitier start new Instance[MultitierApi.Client](
    connect[MultitierApi.Server] { TCP(Tools.resolveIp(Service), 4577) }
  )
}
object Peer extends App {
  loci.multitier start new Instance[MultitierApi.Peer](
    listen[MultitierApi.Peer] { TCP(3, Tools.publicIp) }
  )
  loci.multitier start new Instance[MultitierApi.Peer](
    listen[MultitierApi.Peer] { TCP(4, Tools.publicIp) }
  )
}