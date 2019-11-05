package thesis.samples.common

class HttpServer {
  import akka.actor.ActorSystem
  import akka.http.scaladsl.{ConnectionContext, Http}
  import akka.http.scaladsl.server.Route
  import akka.stream.ActorMaterializer

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.{Await, Future}
  import scala.concurrent.duration.Duration

  trait HttpServer {
    def stop(): Unit
  }

  object HttpServer {
    def start(
               route: Route, interface: String, port: Int = 1,
               connectionContext: Option[ConnectionContext] = None): Future[HttpServer] = {
      implicit val system = ActorSystem()
      implicit val materializer = ActorMaterializer()

      val binding =
        connectionContext map {
          Http() bindAndHandle (route, interface, port, _)
        } getOrElse {
          Http() bindAndHandle (route, interface, port)
        }

      def shutdown() = {
        system.terminate
        Await.result(system.whenTerminated, Duration.Inf)
        materializer.shutdown
      }

      binding.failed foreach { _ => shutdown }

      binding map { binding =>
        new HttpServer {
          def stop() = binding.unbind onComplete { _ => shutdown }
        }
      }
    }
  }

}
