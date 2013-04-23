package demo.akka

import akka.actor.ActorLogging
import akka.actor.Actor
import akka.event.Logging
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import akka.actor.ActorInitializationException
import akka.actor.ActorKilledException

trait Template extends Actor with ActorLogging with LineHandler {
  
  private val name = Logging.simpleName(this)

  override val supervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException ⇒ Stop
    case _: ActorKilledException         ⇒ Restart
    case _: Exception                    ⇒ Restart
  }

  override def preStart() { log.info(s"$name started") }
  override def preRestart(thr: Throwable, msg: Option[Any]) {}
  override def postRestart(thr: Throwable) { log.warning(s"$name restarted due to {}", thr.getClass.getName) }
  override def postStop() { log.info(s"$name stopped") }

}