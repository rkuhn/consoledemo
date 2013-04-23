package demo.akka

import akka.actor._
import akka.pattern.ask
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.Future
import demo.akka.Commander.Line

object ConsoleDemo extends App {
  val system = ActorSystem("Demo")
  implicit val ec = system.dispatcher

  val commander = system.actorOf(Props[Commander].withDispatcher("disp"), "commander")
  
  commander ! Line("create fred Ticker")
  commander ! Line("create barney Sink")
  commander ! Line("send fred add ../barney")

  val handleLine: (String) ⇒ Future[Unit] = (line) ⇒ line.trim match {
    case "exit" ⇒ Future.successful(system.shutdown())
    case cmd ⇒
      commander ! Commander.Line(cmd)
      Future(readLine()) flatMap handleLine
  }

  handleLine(readLine())
}
