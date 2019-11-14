/**
 * This is an example for how an API gateway that takes requests and directs them to the underlying services could look like.
 * This is listing 02 in the thesis.
 */
package thesis.listings.APIGateway.gateway.api

import loci.contexts.Pooled.Implicits.global
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, MethodRejection, RejectionHandler, Route}
import akka.stream.ActorMaterializer
import loci._
import loci.transmitter.rescala._
import loci.serializer.upickle._
import loci.communicator.tcp._
import loci.container._
import rescala.default._

import thesis.listings.APIGateway.timeservice.{Formatter, GatewayApi, MultitierApi}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, duration}

@multitier @containerize object ConsumerApi extends GatewayApi{
  @peer type Gateway <: { type Tie <: Optional[MultitierApi.Formatter] }

  private def asyncCall[T](f : Option[Future[T]]): String = f match {
      case Some(f: Future[T]) => Await.result(f, Duration(5L, duration.SECONDS)).toString
      case None => "Error: Could not retrieve data from Service."
    }

  /**
   * Calls to the underyling {@link thesis.listings.APIGateway.timeservice.TimeService} service.
   */
  private def getCurrentTime : String on Gateway = on[Gateway]{ implicit! => asyncCall ((remote call MultitierApi.getCurrentTime).asLocal) }
  private def getCurrentDate : String on Gateway = on[Gateway] { implicit! => asyncCall ((remote call MultitierApi.getCurrentDate).asLocal) }
  private def getCurrentDateTime : String on Gateway = on[Gateway] { implicit! => getCurrentDate + ", " + getCurrentTime } // aggregation
  private def getFormattedDateTime : String on Gateway = on[Gateway] { implicit! => asyncCall ((remote call MultitierApi.getCurrentFormattedDateTime).asLocal) }

  var services : Int on Gateway = placed{ implicit! => 0 }

  on[Gateway] { implicit ! =>
    remote[MultitierApi.Formatter].joined observe { implicit ! => services += 1 }
    remote[MultitierApi.Formatter].left observe { implicit ! => services -= 1 }
  }
  def main() : Unit on Gateway = on[Gateway]{ implicit! =>
    implicit val system : ActorSystem = ActorSystem("api_gateway")
    implicit val materializer : ActorMaterializer = ActorMaterializer()
    /**
     * exception handlers, a.o. 404.
     */
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

    /**
     * The frontend html page.
     */
    def html(gatewayType : this.GATEWAY_API_TYPE) : String =
      s"""
        |<h3>You have contacted the TimeService API Gateway - This is the gateway for ${ gatewayType match{ case this.GATEWAY_API_CONSUMER => "consumers" case this.GATEWAY_API_BUSINESS => "business customers" } }.</h3>
        |<p>This Gateway is currently connected to $services formatted service instances.</p>
        |<p>The following GET operations are available:</p>
        |<ul>
        | <li><a href="/time">/time</a> => get current time.</li>
        | <li><a href="/date">/date</a> => get current date.</li>
        | <li><a href="/datetime">/datetime</a> => get current date and time.</li>
        | <li><a ${ if(gatewayType == this.GATEWAY_API_BUSINESS) "href=\"/format\"" else "" }>/format</a> => ${ gatewayType match{ case this.GATEWAY_API_CONSUMER => "Not available for consumers" case this.GATEWAY_API_BUSINESS => "get current date and time, nicely formatted" } }.</li>
        |</ul>
        |""".stripMargin

    /**
     * Callable GET routes.
     */
    def IndexRoute(gatewayType : this.GATEWAY_API_TYPE) =
      get {
        pathSingleSlash {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, html(gatewayType)))
        } ~
        path("time"){
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, getCurrentTime.toString))
        } ~
          path("date"){
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, getCurrentDate.toString))
          } ~
          path("datetime"){
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, getCurrentDateTime.toString))
          }
      }
    /**
     * Providing two different API's.
     */
    val consumerRoutes = IndexRoute(this.GATEWAY_API_CONSUMER)
    val businessRoutes = IndexRoute(this.GATEWAY_API_BUSINESS) ~
      get  {
        path("format"){
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, getFormattedDateTime.toString))
        }
      }
    val bindingFutures = List{
      Http().bindAndHandle(Route.handlerFlow(consumerRoutes), Tools.publicIp, 8080)
      Http().bindAndHandle(Route.handlerFlow(businessRoutes), Tools.publicIp, 8081)
    }

    println(s"API Gateway - consumer API - online at http://localhost:8080/")
    println(s"API Gateway - business API - online at http://localhost:8081/")
    while(scala.io.Source.stdin.getLines.next() != "quit"){}

    bindingFutures foreach { b => // cleanup on shutdown
        b.flatMap(_.unbind())
        b.onComplete(_ => system.terminate())
      }
  }
}
@gateway(
  """{
    |  "ports": "8080,8081"
    |}"""
) object APIGateway extends App {
  loci.multitier start new Instance[ConsumerApi.Gateway](
    connect[MultitierApi.Formatter] { TCP(Tools.resolveIp(Formatter), 8153) } //and
      //connect[MultitierApi.Manipulator] { TCP("localhost", 43083) }
  )
}