package thesis.listings.APIGateway.timeservice

import loci._
import loci.container._
import loci.transmitter.rescala._
import loci.serializer.upickle._
import loci.communicator.tcp._
import rescala.default._
import java.util.{Calendar, Date}
import java.text.SimpleDateFormat

import thesis.listings.APIGateway.gateway.api.ConsumerApi

@multitier trait GatewayApi {
  @peer type Gateway

  protected final type GATEWAY_API_TYPE = Int
  protected final val GATEWAY_API_CONSUMER : GATEWAY_API_TYPE = 0
  protected final val GATEWAY_API_BUSINESS : GATEWAY_API_TYPE = 1
}
/**
 * Public Api
 */
@multitier protected trait Api{
  /**
   * Services
   */
  @peer type Peer
  @peer type Manipulator <: Peer
  @peer type Formatter <: Peer
  @peer type Server <: Peer

  lazy final val version = 1.0
  /**
   * Api provided by Server Service
   */
  val time : Var[Long] on Server = Var(0l)

  /**
   * Api provided by Client Service
   */
  //def getTime() : String on Client
}

/**
 * Formatter implementation
 */
@multitier protected[thesis] trait ManipulatorImpl extends Api with GatewayApi{
  @peer type Manipulator <: Peer { type Tie <: Single[Formatter] }

  /**
   * api of this service
   */
  //def shiftTime() : Date on Manipulator = placed{ new Date() }
}
/**
 * Client implementation
 */
@multitier protected[thesis] trait FormatterImpl extends Api with GatewayApi{
  @peer type Formatter <: Peer { type Tie <: Single[Server] with Single[Manipulator] with Optional[ConsumerApi.Gateway] }

  val clientTime : Local[Signal[Long]] on Formatter = placed { Signal{ time.asLocal() } }

  def getSimpleTimeFormat : SimpleDateFormat = { new SimpleDateFormat("hh:mm:ss") }
  def getSimpleDateFormat : SimpleDateFormat = { new SimpleDateFormat("MM/d/yyyy") }
  def getExtendedFormat : SimpleDateFormat = { new SimpleDateFormat("E, d MMMM yyyy, hh:mm:ss a, z") }

  /**
   * api of this service
   */
  def getCurrentTime : String on Formatter = on[Formatter] { implicit! => getSimpleTimeFormat format new Date(clientTime.now) }
  def getCurrentDate : String on Formatter = on[Formatter] { implicit! => getSimpleDateFormat format new Date(clientTime.now) }
  def getCurrentFormattedDateTime : String on Formatter = on[Formatter] { implicit! => getExtendedFormat format new Date(clientTime.now) }

  on[Formatter] {

    val display = Signal { (new SimpleDateFormat("hh:mm:ss")) format new Date(clientTime.apply) }
    clientTime.changed observe { x =>
      println(getCurrentFormattedDateTime)
    }
  }
}
/**
 * Service implementation
 */
@multitier protected[thesis] trait ServerImpl extends Api {
  @peer type Server <: Peer { type Tie <: Multiple[Formatter] }

  def main() : Unit on Server = {
    while (true) {
      time set Calendar.getInstance.getTimeInMillis
      Thread sleep 1000
    }
  }
}
@multitier object MultitierApi extends ServerImpl with FormatterImpl with ManipulatorImpl

@service
object Server extends App {
  loci.multitier start new Instance[MultitierApi.Server](
    listen[MultitierApi.Formatter] {
      TCP(43062, Tools.publicIp)
    }
  )
}
@service  object Formatter extends App {
    loci.multitier start new Instance[MultitierApi.Formatter](
      connect[MultitierApi.Server] {
        TCP(Tools.resolveIp(Server), 43062)
      } and
        connect[MultitierApi.Manipulator] {
          TCP(43063, Tools.publicIp).firstConnection
        } and
        connect[ConsumerApi.Gateway] {
          TCP(43082, Tools.publicIp).firstConnection
        }
    )
  }
@service object Manipulator extends App {
  loci.multitier start new Instance[MultitierApi.Manipulator](
    connect[MultitierApi.Formatter] { TCP(Tools.resolveIp(Formatter), 43063) } //and
      //connect[thesis.components.gateway.api.ConsumerApi.Gateway] { TCP(43083, Tools.publicIp).firstConnection }
  )
}