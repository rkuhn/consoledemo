package demo.akka

import akka.actor.ActorLogging
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ExtendedActorSystem

trait LineHandler { this: Actor with ActorLogging ⇒

  type LineRecv = PartialFunction[List[String], Unit]

  def lineRecv: LineRecv

  private val reflector = context.system.asInstanceOf[ExtendedActorSystem].dynamicAccess

  def handleLine: Receive = {
    val lr = lineRecv

    {
      case Commander.Line(line) ⇒
        line.split("\\s+").toList match {
          case "identify" :: Nil ⇒
            log.info(s"I’m a ${getClass.getName} with children ${context.children.map(_.path.name).mkString("[", ", ", "]")}")
          case "create" :: name :: clazz :: args ⇒
            context.actorOf(
              Props(
                reflector.createInstanceFor[Actor](s"demo.akka.$clazz", args map (a ⇒ (classOf[String], a))).get)
                .withDispatcher("disp"),
              name)
          case tokens ⇒
            if (lr.isDefinedAt(tokens)) lr(tokens)
            else log.warning("unhandled command [{}]", line)
        }
    }
  }

}