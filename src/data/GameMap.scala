package data

import networking.Server._
import game._
import Main._
import scala.collection.mutable._
import java.io._
//
//class BSTMap[Z, P](comp: (Z, Z) => Int) extends Map[Z, P] {
//  class Node(var zip: Z, var place: P, var left: Node, var right: Node)
//  private var root: Node = null
//
//  def get(zip: Z): Option[P] = {
//    var rover = root
//    while (zip != rover.zip && rover != null) {
//      val c = comp(zip, rover.zip)
//      if (c < 0) rover = rover.left
//      if (c > 0) rover = rover.right
//    }
//    if (rover == null) None else Some(rover.place)
//  }
//
//  def +=(zp: (Z, P)) = {
//    val (zip, place) = zp
//    def recur(n: Node): Node = {
//      if (root == null) {
//        new Node(zip, place, null, null)
//      } else {
//        val c = comp(zip, n.zip)
//        if (c < 0) n.left = recur(n.left)
//        if (c > 0) n.right = recur(n.right)
//        if (c == 0) n.place = place
//      }
//      n
//    }
//    root = recur(root)
//    this
//  }
//
//  def -=(zip: Z) = this
//  def iterator = new Iterator[(Z, P)] {
//    def next = null
//    def hasNext = false
//  }
//}

class Place(val zip: Int, val name: String, val exts: Buffer[Place], val helper:Buffer[String], val itms: Buffer[String]) extends Serializable {
  private var des: String = null
  private var containPlayers: Buffer[User] = Buffer()

  def playersHere: Buffer[User] = containPlayers

  def updateCPlayers(luser: User, mode: Char) {
    if (mode == '+') containPlayers.append(luser)
    if (mode == '-') containPlayers = containPlayers - luser
  }

  def updateDes(s: String): Unit = des = s
  def item: Buffer[String] = itms
  def addItem(itm: String) = itms.append(itm)
  def removeItem(itm: String) = itms.remove(itms.indexOf(itm))

  def countPath(p: Place, cnt: Int): Int = {
    while (this != p && this.exts.isEmpty) {
      this.exts.foreach(e => countPath(e, cnt + 1))
    }
    cnt
  }

  def addPlaceToCurrent(p: Place) {
    this.exts.append(p)
    p.exts.append(this)
  }
}

object GameMap {
  val p1: Place = new Place(9998, "president's office, tinity university", Buffer[Place](), Buffer[String]("president", "secretary"), Buffer[String]())
  val p2: Place = new Place(5000, "san antonio airport", Buffer[Place](), Buffer[String](),Buffer[String]("cyanoacrylate"))
  val p3: Place = new Place(15000, "cancun airport", Buffer[Place](), Buffer[String](), Buffer[String]())
  val p4: Place = new Place(9999, "meybee dining hall, tinity university", Buffer[Place](), Buffer[String]("jani"), Buffer[String]("round plate", "coke", "square plate", "triangle plate", "tall plate", "wood plate", "dirty plate"))
  val p5: Place = new Place(10000, "reception, tinity university", Buffer[Place](), Buffer[String](), Buffer[String]("cigarettes"))
  val p6: Place = new Place(15001, "lobby, taco inn", Buffer[Place](), Buffer[String]("jane"), Buffer[String]())
  val p7: Place = new Place(9997, "jane's dorm, tinity university", Buffer[Place](), Buffer[String](), Buffer[String]("diary"))
  
  p1.exts.append(p5)
  p2.exts.append(p5, p3)
  p3.exts.append(p2, p6)
  p4.exts.append(p5)
  p5.exts.append(p2, p4, p1, p7)
  p6.exts.append(p3)
  p7.exts.append(p5)
  
  p7.item
  //  p1.exts.append(p4, p2)
//  p2.exts.append(p1, p3)
//  p3.exts.append(p2)
//  p4.exts.append(p1, p2)

  val places: Buffer[Place] = Buffer(p1, p2, p3)

  val origMap: Map[String, Place] = Map(
    "president's office, tinity university" -> p1,
    "SAT airport" -> p2,
    "MEX airport" -> p3)

  def placeExists(p: String): Boolean = origMap.contains(p)
  def erasable(zip: Int): Boolean = true

  def addPlace(user: User, place: Place, zip: Int) = {
    var rover = origMap("terminal")
    def recur {
      if (zip < rover.zip) {

      }
    }

    //    val p1 = origMap(zip)
    //    place.exits.append(p1)
    //    p1.exits.append(place)
    //    origMap += place.name -> place
    //    user.os.println("new place found!")
    //    var root = 
  }
  //
  //  def loadMap(option: String, user: User) {
  //    option match {
  //      case "get" => {
  //        val is = new ObjectInputStream(new FileInputStream(mapFile))
  //        is.readObject match {
  //          case p: Map[String, Place] => {
  //            val origMap = p
  //          }
  //          case user: User => user.os.println("load user")
  //          case _ => user.os.println("failed to read map!")
  //        }
  //        is.close()
  //      }
  //      case "save" => {
  //        val os = new ObjectOutputStream(new FileOutputStream(mapFile))
  //        os.writeObject(origMap)
  //        os.close()
  //      }
  //      case "original" => {
  //        val os = new ObjectOutputStream(new FileOutputStream(mapFile))
  //        os.writeObject(origMap)
  //        println(origMap)
  //        os.close()
  //      }
  //      case _ => user.os.println("say what?")
  //    }
  //  }
}

