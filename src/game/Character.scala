package game

import networking.Server._
import game.Item._
import Case._
import Run._
import data.GameMap._
import data.Main._
import scala.collection.mutable.Buffer
import scala.io.Source
import data.Main
import data.Place
import data.GameMap
import networking.Server
import scala.util.Random
import actors.Actor.actor

class Detective(val name: String, private val pw: String, private var exp: Double, private var inventory: List[String], private var location: Place) {
  private var createRoom = false

  def cr = createRoom
  def changeCreateRoom = createRoom = !createRoom

  def addInventory(newInv: String) {
    inventory = newInv :: inventory
  }
  def inv:List[String] = inventory

  def l: Place = location
  def updateLocation(newPlace: Place) = location = newPlace

  def getEXP: Double = exp
  def addEXP(up: Double) = exp += up

  def getPW: String = pw
}

object Detective {
  def go(luser: User, sub: String) {
    try {
      val content = sub.toInt
      if (content <= luser.id.l.exts.size) {
        luser.id.l.updateCPlayers(luser, '-')
        luser.id.l.playersHere.foreach(_.os.println(luser.name + " has left the room."))
        luser.id.updateLocation(luser.id.l.exts(content - 1))
        luser.id.l.playersHere.foreach(_.os.println(luser.name + " has entered the room."))
        luser.id.l.updateCPlayers(luser, '+')
      } else {
        luser.os.println("you bumped into solid concrete.")
      }
      basicStatus(luser)
    } catch {
      case num: java.lang.NumberFormatException => luser.os.println("alalal")
    }
  }

  def duel(user1: User, user2: User) {
    val u1EXP = user1.id.getEXP
    val u2EXP = user2.id.getEXP
    if (math.abs(u1EXP - u2EXP) > 50) user1.os.println("don't think about it. he/she is so much stronger than you")
    else {
      user1.os.println("index number of the case you want to solve: ")
      val c = user1.is.readLine.trim.toInt
      user2.os.println(user1.id.name + " challenges you. do you take it? (y/n) ")
      if (user2.is.readLine() == "y") {
        val both = Array[User](user1, user2)
        both.foreach(_.os.println("duel started"))
        var flag1 = false
        var flag2 = false
        duelMode(user1, c)
        duelMode(user1, c)
        while (!flag1 && !flag2) {

        }
        if (flag1) { user1.os.println("you won! :)"); user2.os.println("you lost! :(") }
        else { user2.os.println("you won! :)"); user1.os.println("you lost! :(") }
      } else user1.os.println("invitation refused")
    }
  }

  def duelMode(luser: User, c: Int): Boolean = {
    solveCase(luser, c)
    true
  }

  def solveCase(user: User, caseIndex: Int) {
    if (cases.isEmpty) {
      user.os.println("no cases. it's rather peaceful.")
    } else {
      val lcase = cases(caseIndex)
      var solvedP = 0.0
      var turns = 0
      //      val res = user.is.readLine
      while (solvedP < 100) {
        val rand = Random.nextDouble
        val delta = ((user.id.exp / lcase.getDiff) * Random.nextDouble * 100)
        if (rand > 0.3 && rand < 0.6) {
          user.os.print("> someone gave you a lead...do you trust it? (y/n) ")
          val flag = if (user.is.readLine().contains('y')) true else false
          (flag, Random.nextBoolean) match {
            case (true, true) => {
              user.os.print("> sweet! it was very helpful! ")
              solvedP += delta + 10
            }
            case (true, false) => {
              user.os.print("> too bad, it was the culprit's attempt to fool you. ")
              solvedP -= delta - 5
            }
            case (false, true) => user.os.print("> you should've listened! ")
            case (false, false) => user.os.print("> good one! it was the culprit's attempt to fool you. ")
          }
        } else {
          if (rand < 0.3 && solvedP - delta > 0) {
            user.os.print("> oh man, it was a dead end. ")
            solvedP -= delta
          } else {
            user.os.print("> found a crucial evidence! ")
            solvedP += delta
          }
        }
        if (solvedP.toInt > 100) {
          user.os.println("\n> chasing the culprit down the street. alomst!")
        } else if (solvedP.toInt < 0) {
          user.os.println("you're really confused (" + solvedP.toInt + "%).")
        } else user.os.println("(" + solvedP.toInt + "%) ")
        user.os.println(". . .")
        turns += 1
        Thread.sleep((Random.nextDouble * 5000).toLong)
      }
      val bpGain = lcase.getDiff * 0.1 * turns
      user.os.println("you've solved the case! bp gained: " + (bpGain).toInt)
      user.id.addEXP(bpGain)
    }
  }

  def pick(user: User, content: String) {
    if (user.id.l.item.contains(content)) {
      user.id.addInventory(content)
      user.id.l.removeItem(content)
      user.os.println("you picked up " + content)
      if (content == "diary") user.os.println("jane writes: \"cs2 is such a hard class that i had to give up going to cancun this summer :( i wonder when i'll have the chance to stay at the famous taco inn?\"")
      if (content == "square plate") {
        if (user.id.inv.contains("cyanoacrylate")) {
          user.os.println("analyzing the fingerprint using item (cyanoacrylate)")
          user.os.print(". "); Thread.sleep(2000); user.os.print(". "); Thread.sleep(2000); user.os.print(". ")
          user.os.println("3 sets of fingerprints could be found: susan roberts', jani's, the president's")
          user.os.println("who do you think is the murderer?")
          var flag = false
          while (flag == false) {
            val answer = user.is.readLine().trim
            if (answer.contains("president")) {
              printDialog(user, "the police arrested the president of tinity university. he later confessed that he didn't think the lasagna from meybee would be so hard that it would kill susan roberts. he apologized to jane and spent the rest of his life in jail".split(" "))
              user.os.println("your brain power went up by 100!")
              user.id.addEXP(100)
              flag = true
              user.os.println("you've completed lv 1. more big cases are yet to come. until then, you should go around and solve quick cases to gain bp!")
            } else if (answer.contains("jani")) {
              printDialog(user, "the police arrested jani the janitor, but not until later do you realize that jani's fingerprints were on the plate only because he's doing his job, you idiot.".split(" "))
              user.os.println("your brain power went down to 0 as a punishment.")
              user.id.addEXP(-(user.id.exp))
              flag = true
            } else user.os.println("you serious? try again.")
          }
        } else user.os.println("but you don't have the right tool to analyze the plate.")
      }
    } else {
      user.os.println("you can't find " + content + " here.")
    }
  }

  def explrPlace(user: User) {
    if (user.id.cr == true) {
      user.os.println("what's the area code of the new place? (under 10000 is SA, beyond 10000 is mexico)")
      val zip = user.is.readLine
      if (erasable(zip.toInt)) {
        user.os.println("this place is too popular to be abandoned.")
      } else {
        user.os.println("name of this place?")
        val place = new Place(zip.toInt, user.is.readLine, Buffer[Place](), Buffer[String](), Buffer[String]())
        user.os.println("description of the place?")
        place.updateDes(user.is.readLine())
        user.os.println("items? (enter the # of items, separated by\",\"")
        for (i <- 0 until allItems.size) user.os.print(i + 1 + ": " + allItems(i) + " ")
        user.os.println()
        var res = (user.is.readLine).split(",")
        for (n <- 0 until res.length) place.addItem(allItems(res(n).trim.toInt - 1))
        addPlace(user, place, zip.toInt)
        user.os.println("new place found!")
      }
    } else {
      user.os.println("you are not authorized to do so")
    }
  }

  val helpers = Map(
    "president" -> "president: \"you want to know about the case? i mourn for ms.roberts, but the truth is i have no knowledge of this case whatsoever. i wish i could help more . . . but please ask my secretary so she can give you a tour while you're here!\"",
    "secretary" -> "secretary: \"the case? oh poor susan. i remember the night susan died, she had to cancel a meeting to pick up her daughter, jane. maybe she knows something. but no one knows where she went since that night. maybe you can investigate in her dorm room? by the way, you can go to places entering the \"go\" followed by the place index.\"",
    "jane" -> "jane: \"how did you find me??! anyway...i'll tell you what happened since you've proved yourself by finding me - mom and i were having dinner at meybee dining hall. it was completely empty because no one likes the food there. later i went to the restroom and suddenly, i heard people arguing so i ran out - i saw the man hitting mom's head with the lasagna! he escaped before i got there and my mom passed away. but i think he left his fingerprints on the plate when he grabbed the lasagna. maybe you can find something in meybee...\"",
    "jani" -> "jani: \"hi i'm jani the janitor! did you spill your coke agai...you're looking for a plate from two nights ago? no problem sir, we never wash dishes at meybee. it should be the square plate over there.\"")

  def ask(luser: User, prmt: String) {
    if (helpers.contains(prmt)) {
      val words = helpers(prmt).split(" ")
      printDialog(luser, words)
    } else luser.os.println("there's no such person here")
  }

  def printDialog(luser: User, words: Array[String]) {
    luser.os.println()
    for (i <- 0 until words.length) {
      luser.os.print(words(i) + " ")
      Thread.sleep(180)
    }
    luser.os.println()
  }

  def check(m2: String, luser: User) {
    m2 match {
      case "inv" => checkInv(luser)
      case "stat" => checkStat(luser)
      case "man" => checkManual(luser)
      case "dist" => checkDist(luser)
      case "place" => checkPlace(luser)
      case "basic" => basicStatus(luser)
      case _ => luser.os.println("say that again?")
    }
  }

  def checkInv(user: User) {
    if (user.id.inv != null) {
      for (i <- 0 until user.id.inv.length) {
        user.os.println(user.id.inv(i))
      }
    }
  }

  def checkStat(user: User) {
    user.os.println("brain power: " + user.id.getEXP)
  }

  def checkManual(user: User) {
    val scr = Source.fromFile(manFile)
    val lines = scr.getLines()
    while (lines.hasNext) {
      user.os.println(lines.next)
    }
  }

  //  TODO
  def checkDist(user: User) {
    user.os.println("where to? (enter the place name)")
    val res = (user.is.readLine).trim
    if (origMap.contains(res)) {
      //      user.id.l.countPath(origMap(res), 0)
    } else {
      user.os.println("place doesn't exit.")
    }
  }

  def checkPlace(user: User) {
    user.os.println("name of the place?")
    val name = user.is.readLine()
    user.os.println(if (origMap.contains(name)) "the place exists!" else "this place doesn't exist")
  }
}