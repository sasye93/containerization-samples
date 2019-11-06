package thesis.samples.examples

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
    val clientModule: MongoClient = MongoClient(Tools.globalDbIp(MultitierApi))
    val clientService: MongoClient = MongoClient(Tools.localDbIp(Service))

    val dbModule: MongoDatabase = clientModule.getDatabase("mydb")
    val dbService: MongoDatabase = clientService.getDatabase("mydb")

    //dbModule.createCollection("global")
    dbService.createCollection("local")

    val moduleCol = dbModule.getCollection("global")
    val serviceCol = dbService.getCollection("local")

    val doc: Document = bson.Document("name" -> "MongoDB", "type" -> "database", "count" -> 1, "info" -> bson.Document("x" -> 203, "y" -> 102))

    val resultObserver = new Observer[Completed] {
      override def onNext(result: Completed): Unit = println("Inserted")
      override def onError(e: Throwable): Unit = println("Failed")
      override def onComplete(): Unit = println("Completed")
    }
    moduleCol.insertOne(doc).subscribe(resultObserver)
    serviceCol.insertOne(doc).subscribe(resultObserver)
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
@multitier @containerize(
  """{
    |  "app": "timeservice",
    |  "globalDb": "mongo",
    |  "stateful": "true"
    }"""
) object MultitierApi extends ServerImpl with ClientImpl

@service(
  """{
    |  "type": "service",
    |  "localDb": "mongo",
    |  "replicas": 2,
    |  "attachable": "false"
    |}"""
) object Service extends App {
  loci.multitier start new Instance[MultitierApi.Server](
    listen[MultitierApi.Client] { TCP(4577, Tools.publicIp) }
  )
}
@service(
  """{
    |  "type": "service",
    |  "replicas": 3,
    |  "description": "-"
    |}"""
)
object Client extends App {
  loci.multitier start new Instance[MultitierApi.Client](
    connect[MultitierApi.Server] { TCP(Tools.resolveIp(Service), 4577) }
  )
}/*
object Peer extends App {
  loci.multitier start new Instance[MultitierApi.Peer](
    listen[MultitierApi.Peer] { TCP(4578, Tools.publicIp) }
  )
  loci.multitier start new Instance[MultitierApi.Peer](
    listen[MultitierApi.Peer] { TCP(4579, Tools.publicIp) }
  )
}*/