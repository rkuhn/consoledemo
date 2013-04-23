package demo.akka

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSelection
import akka.actor.Cancellable
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import scala.util.control.NoStackTrace
import scala.util.control.NonFatal

object Ticker {
  class DurationFormatException(msg: String, cause: Throwable = null) extends RuntimeException(msg, cause) with NoStackTrace
  case object Tick
}

class Ticker extends Template {

  import Ticker._
  import context.dispatcher

  var targets: Set[String] = Set.empty
  var task: Option[Cancellable] = None

  def lineRecv = {
    case "interval" :: intv :: Nil ⇒
      val interval = Duration(intv) match {
        case f: FiniteDuration ⇒ f
        case x                 ⇒ throw new DurationFormatException("not finite: " + x)
      }
      task foreach (_.cancel)
      task = Some(context.system.scheduler.schedule(interval, interval, self, "tick"))
      log.info("set interval to [{}]", interval)
    case "add" :: target :: Nil ⇒
      targets += target
      printTargets()
    case "remove" :: target :: Nil ⇒
      targets -= target
      printTargets()
  }

  def printTargets() { log.info("targets now " + targets.mkString("[", ", ", "]")) }

  def receive = handleLine orElse {
    case "tick" ⇒ targets foreach (context.actorSelection(_) ! Tick)
  }

}