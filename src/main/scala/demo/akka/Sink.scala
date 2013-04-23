package demo.akka

import akka.actor.ActorLogging
import akka.actor.Actor
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration

class Sink extends Template {

  var duration = Duration.Zero

  override def preStart() { log.info("Sink started") }
  override def preRestart(thr: Throwable, msg: Option[Any]) {}
  override def postRestart(thr: Throwable) { log.warning("Sink restarted due to {}", thr.getClass.getName) }
  override def postStop() { log.info("Sink stopped") }

  def lineRecv = {
    case "duration" :: dur :: Nil ⇒
      duration = Duration(dur) match {
        case f: FiniteDuration ⇒ f
        case x                 ⇒ throw new Ticker.DurationFormatException("not finite: " + x)
      }
  }

  def receive = handleLine orElse {
    case msg ⇒
      log.info("received [{}]", msg)
      Thread.sleep(duration.toMillis)
  }

}