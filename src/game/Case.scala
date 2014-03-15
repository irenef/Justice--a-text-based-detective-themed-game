package game

import networking.Server._
import data.Main._
import data.GameMap._
import scala.util.Random
import actors.Actor.actor
import scala.collection.mutable._
import data.Main
import data.Place
import data.GameMap
import networking.Server

class Case(val number: Int, private var diff: Double, private var location: Place) {
//  val moves: Array[User => Unit] = Array(
//    luser => intensify(luser), luser => relocate(luser))

  //normal methods
  def getLocation: Place = location
  def getDiff: Double = diff

  //  movements 
//  def intensify(user: User) {
//    if (diff < 50) {
//      diff += Random.nextInt(5)
//      user.os.println("case #" + number + " has intensified")
//    } else {
//      user.os.println("case #" + number + " has become an international case")
//    }
//  }
//
//  def relocate(user: User) {
//    user.os.println("case #" + number + " has relocated")
//  }
//
//  def randomMove(user: User) {
//    val rand = Random.nextInt(moves.size)
//    moves(rand)(user)
//  }
}

object Case {
  val cases: Buffer[Case] = Buffer()

  def startCase(user: User) = {
    actor {
      val c = new Case(cases.size,
        Random.nextInt(10),
        places(Random.nextInt(places.size))
        )
      cases.append(c)
      while (true) {
    //    c.randomMove(user)
        Thread.sleep(20000)
      }
    }
  }
  
    def localCases(user: User): Buffer[Case] = {
    val lc: Buffer[Case] = Buffer()
    if (!cases.isEmpty) {
      for(i <- 0 until cases.length) {
        if(cases(i).location == user.id.l) lc.append(cases(i))
      }
      lc
    } else { null }
  }
}