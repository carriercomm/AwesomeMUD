package awesomeware.core.entities

import awesomeware.core.DescType._
import awesomeware.core.Container
import awesomeware.core.DescType
import scala.Predef._
import scala.Some

case class RoomExit(from: Room, to: Container)

class ContainerExit(val names: Set[String], val from: Container, val to: Container) {

}

class Room extends GameEntity with Container {
  var description: String = "An empty room."

  protected var exits: Set[ContainerExit] = Set()

  def getAllExits(): Set[String] = {
    exits.flatMap(x => x.names)
  }

  def getExit(name: String): Option[ContainerExit] = {
    val lowerName = name.toLowerCase
    val valid: Set[ContainerExit] = exits.filter({
      exit => exit.names.contains(lowerName)
    })
    valid.size match {
      case 0 => None
      case _ => Some(valid.head)
    }
  }

  def describeTo(to: GameEntity, dType: DescType): String = {
    var b = new StringBuilder()

    b ++= s"$name\n"
    b ++= "-" * name.length() + "\n"
    b ++= "Exits: "

    exits.size match {
      case 0 =>
        b ++= "None\n"
      case _ =>
        for (exit <- exits) {
          val properName = exit.names.head(0).toUpper + exit.names.head.substring(1)
          b ++= properName
          b ++= ","
        }
        b.deleteCharAt(b.length - 1) // Remove last ","
        b ++= "\n"
    }

    b ++= description + "\n"
    b ++= "-" * name.length() + "\n"

    return b.toString
  }

  override def entered(obj: GameEntity, from: Container) {
    for (e <- (inventory diff List(obj))) {
      e.receiveText(s"$obj entered from $from")
    }
    obj.receiveText(this.describeTo(obj, DescType.LongDesc))
  }

  override def exited(obj: GameEntity, to: Container) {
    for (e <- inventory) {
      e.receiveText(s"$obj left towards $to")
    }
  }
}