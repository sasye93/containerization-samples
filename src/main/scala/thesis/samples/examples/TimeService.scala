/**
 * This is a containerized version of the TimeService example.
 */
package thesis.samples
package timeservice

import loci._
import loci.transmitter.rescala._
import loci.serializer.upickle._
import loci.communicator.tcp._
import rescala.default._
import java.util.{Calendar, Date}
import java.text.SimpleDateFormat

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
@multitier protected[thesis] trait ServerImpl extends Api{
  @peer type Server <: Peer { type Tie <: Multiple[Client] }
  val time: Var[Long] on Server = on[Server] { implicit! => Var(0L) }

  def main() : Unit on Server = placed{ implicit! =>
    while (true) {
      time set Calendar.getInstance.getTimeInMillis
      Thread sleep 1000
    }
  }
}
@multitier @containerize(
  """{
    |  "app": "timeservice",
    |  "jreBaseImage": "jre-alpine"
    |}"""
) object MultitierApi extends ServerImpl with ClientImpl

@service(
  """{
    |  "type": "service",
    |  "replicas": 2,
    |  "attachable": "false"
    |}"""
) object Service extends App {
  loci.multitier start new Instance[MultitierApi.Server](
    listen[MultitierApi.Client] { TCP(8191, Tools.publicIp) }
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
    connect[MultitierApi.Server] { TCP(Tools.resolveIp(Service), 8191) }
  )
}
/*
object Peer extends App {
  loci.multitier start new Instance[MultitierApi.Peer](
    listen[MultitierApi.Peer] { TCP(4578, Tools.publicIp) }
  )
  loci.multitier start new Instance[MultitierApi.Peer](
    listen[MultitierApi.Peer] { TCP(4579, Tools.publicIp) }
  )
}*/