/**
  * This is an example for how to separate implementations using interfaces,
  * so that services are separately deployable.
  * This is listing 01 in the thesis.
  */
package thesis.listings.Interfaces

import loci._
import loci.transmitter.rescala._
import loci.serializer.upickle._
import loci.communicator.tcp._
import rescala.default._
import java.util.{Calendar, Date}
import java.text.SimpleDateFormat

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
package ClientService {

  @multitier protected[thesis] trait ClientImpl extends Api {
    @peer type Client <: Peer { type Tie <: Single[Server] }

    on[Client] { implicit! =>
      val display = Signal { (new SimpleDateFormat("hh:mm:ss")) format new Date(time.asLocal()) }
      display.changed observe println
    }
  }
}
/**
 * Service implementation
 */
package ServerService {

  @multitier protected[thesis] trait ServerImpl extends Api {
    @peer type Server <: Peer { type Tie <: Multiple[Client] }
    val time: Var[Long] on Server = on[Server] { implicit! => Var(0L) }

    def main() : Unit on Server = placed{ implicit! =>
      while (true) {
        time set Calendar.getInstance.getTimeInMillis
        Thread sleep 1000
      }
    }
  }
}
@multitier object MultitierApi extends ServerService.ServerImpl with ClientService.ClientImpl

object Server extends App {
  loci.multitier start new Instance[MultitierApi.Server](
    listen[MultitierApi.Client] { TCP(43059) }
  )
}
object Client extends App {
  loci.multitier start new Instance[MultitierApi.Client](
    connect[MultitierApi.Server] { TCP("localhost", 43059) }
  )
}