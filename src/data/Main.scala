package data

import networking.Server._
import GameMap._
import actors.Actor.actor

object Main {
  val port = 4455 
  val startPlace = origMap("president's office, tinity university")
  val manFile = "man.txt"
  val mapFile = "map.bin"
  val usersRecFile = "usersRec.bin"

  def main(args: Array[String]) {
    actor { startServer(port) }
  }
}
