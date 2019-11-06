package thesis.samples
package chat

import loci._
import loci.transmitter.rescala._
import loci.serializer.upickle._
import loci.communicator.tcp._
import rescala.default._

import loci.container.Tools._
import loci.container._
import org.mongodb.scala.bson.BsonString
import thesis.samples.common.Db

package object db{
  lazy val getDb : Db = new Db(Tools.localDbIp(Server), "chat") //todo on Server
}
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

  val message : Evt[String] on Client
  val publicMessage : Evt[String] on Server
}
/**
  * Client implementation
  */
@multitier protected[thesis] trait ClientImpl extends Api {
  @peer type Client <: Peer { type Tie <: Single[Server] }

  val message : Evt[String] on Client = on[Client] { implicit ! => Evt[String] }

  on[Client] { implicit ! =>
    publicMessage.asLocal observe println
  }

  def main() : Unit on Client = placed{ implicit ! =>
    //publicMessage.asLocal observe println

    for (line <- scala.io.Source.stdin.getLines)
      message.fire(line)
  }
}
/**
  * Service implementation
  */
@multitier protected[thesis] trait ServerImpl extends Api{
  @peer type Server <: Peer { type Tie <: Multiple[Client] }

  on[Server]{ implicit ! =>
    message.asLocalFromAllSeq.observe((t : Tuple2[Remote[Client], String]) => {
      db.getDb.insertDocument("messages", Seq({ "message" -> BsonString(t._2) }))
      db.getDb.getDocuments("messages", publicMessage)
    })
  }
  val publicMessage : Evt[String] on Server = on[Server]{ implicit ! => Evt[String] }
}
//todo change 'home' to your project location.
@multitier @containerize(
  """{
    |  "app": "chat",
    |  "home": "C:\Users\Simon S\Desktop\containerization-samples",
    |  "secrets": "(mysecret,files\secrets)|(mysecret2,files\secrets)"
    |}"""
) object MultitierApi extends ServerImpl with ClientImpl

@service(
  """{
    |  "localDb": "mongo",
    |  "secrets": "mysecret",
    |  "replicas": 2
    |}"""
) object Server extends App {
  multitier start new Instance[MultitierApi.Server](
    listen[MultitierApi.Client] { TCP(43053, Tools.publicIp) })
}
@service(
  """{
    |  "replicas": 2
    |}"""
) object Client extends App {
  multitier start new Instance[MultitierApi.Client](
    connect[MultitierApi.Server] { TCP(Tools.resolveIp(Server), 43053) })
}