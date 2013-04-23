package demo.akka

import akka.actor.Actor
import akka.actor.Kill

object Commander {
  case class Line(line: String)
}

class Commander extends Template {

  import Commander._

  override def postStop() { context.system.shutdown() }

  def lineRecv = {
    case "send" :: path :: msg ⇒ context.actorSelection(path) ! Line(msg.mkString(" "))
    case "kill" :: path :: Nil ⇒ context.actorSelection(path) ! Kill
    case "stop" :: path :: Nil ⇒ context.stop(context.actorFor(path))
  }

  def receive = handleLine

}