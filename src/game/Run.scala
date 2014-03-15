package game

import networking.Server._
import Case._
import Detective._
import java.io.PrintStream
import scala.collection.mutable.Buffer
import scala.io.Source
import networking.Server

object Run {
  val CommandSplit = """\s*(\w+)(\s+(.*))?\s*""".r

  val cmdMap: Map[String, (String, User) => Any] = Map(
    "go" -> ((m2, luser) => go(luser, m2)),
    "case" -> ((m2, luser) => solveCase(luser, m2.toInt)),
    "pick" -> ((m2, luser) => pick(luser, m2)),
  //  "explr" -> ((m2, luser) => explrPlace(luser)),
    "check" -> ((m2, luser) => check(m2, luser)),
    "duel" -> ((m2,luser) => duel(luser,users(m2))),
    "ask" -> ((m2, luser)=> ask(luser, m2)))

  def processCmd(user: User, input: String) {
    try {
    val CommandSplit(command, _, subject) = input
    if (cmdMap.contains(command)) cmdMap(command)(subject, user)
    else user.os.println("invalid input")
    } catch {
      case me:MatchError => user.os.println("invalid input")
    }
  }

  def basicStatus(luser: User) {
    luser.os.println
    //    location
    luser.os.println("your location: " + luser.id.l.name)
    //    items
    if (luser.id.l.item != null) {
      luser.os.println("items: ")
      for (i <- 0 until luser.id.l.item.length) {
        luser.os.println("  " + luser.id.l.item(i))
      }
    }
    //	  local cases
    printLocalCase(luser)
    //    other detectives 
    val localDets = luser.id.l.playersHere
    luser.os.println("people in this room: ")
    luser.id.l.helper.foreach(n => luser.os.print("["+n+"] "))
    localDets.foreach(d => if(d.id.name != luser.id.name) luser.os.print("'"+d.id.name+"' "))
    luser.os.println()
    //    exits
    luser.os.println("exit:")
    for (i <- 0 until luser.id.l.exts.length) {
      luser.os.println("  " + (i + 1) + ": "
        + luser.id.l.exts(i).name)
    }
    luser.os.println("what now?")
    luser.os.println("enter \"check man\" to check manual")
  }

  def printLocalCase(luser: User) {
    val lc = localCases(luser)
    if (lc != null) {
      luser.os.println("local cases: ")
      lc.foreach(c => luser.os.println("  case " + c.number))
    }
  }
}