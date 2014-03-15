package networking

import game.Run._
import data.FixedRecordSeq._
import data.Main._
import game.Case._
import data.GameMap._
import game._
import java.net.Socket
import collection.mutable
import java.net.ServerSocket
import actors.Actor.actor
import java.io._
import scala.annotation.tailrec
import scala.collection.mutable.Buffer

object Server {
  class User(val number: Int, val name: String, val sock: Socket, val is: BufferedReader, val os: PrintStream, val id: Detective)
  val users = new mutable.HashMap[String, User] with mutable.SynchronizedMap[String, User]
  //  private var user: User = null

  def startServer(port: Int) {
    val ss = new ServerSocket(port)
    while (true) {
      val sock = ss.accept
      actor { addUser(sock) }
    }
  }

  private def addUser(sock: Socket) {
    val is = new BufferedReader(new InputStreamReader(sock.getInputStream()))
    val os = new PrintStream(sock.getOutputStream())
    os.println("Welcome to Justice! log in/new game?")
    os.flush
    var flag = false
    var response = is.readLine().trim
    while (!flag)
      response match {
        case "new" | "new game" => {
          os.print("new username: ")
          os.flush()
          val name = checkNameAvailability(is, os)
          os.print("set password: ")
          val pw = is.readLine()
          val det = new Detective(name, pw, 1, List(), startPlace)
          os.println("do you have to right the create rooms? y/n")
          if (is.readLine == "y") det.changeCreateRoom
          println(users.size + 1)
          val user = new User(users.size + 1, name, sock, is, os, det)
          users += name -> user
          val intro = ("you: \"it's been 5 years since i've visited tinity university, my alma mater. but today i'm not here to be nostalgic - two days ago, the vice president susan roberts, who's also my best friend in college, was killed in meybee dining hall. im here, heart broken, wanting to find the truth.\"").split(" ")
          val prompt1 = "\"anyway...i should talk to the president first.\"".split(" ")
          for (i <- 0 until intro.length) {
            os.print(intro(i) + " ")
            Thread.sleep(190)
          }
          os.println
          for (y <- 0 until prompt1.length) {
            os.print(prompt1(y) + " ")
            Thread.sleep(190)
          }
          os.println("(enter \"ask president\" to talk)")
          os.flush
          flag = true
          Thread.sleep(2000);
          basicStatus(user)
          startGame(user)
          flag = true
        }
        case "log" | "log in" => {
          os.print("username: ")
          val name = is.readLine.trim
          if (name == "quit") sock.close()
          if (users.contains(name)) {
            os.print("password: ")
            val pw = is.readLine.trim
            if (pw == "quit") sock.close()
            val tUserID = usersRecord(users(name).number)
            //            usersRecord.close()
            if (pw == tUserID.getPW) {
              val tUser = new User(users(name).number, name, sock, is, os, tUserID)
              basicStatus(tUser)
              startGame(tUser)
            }
            flag = true
          } else {
            os.println("username doesn't exist.")
          }
        }
        case "quit" | "exit" => {
          sock.close()
          flag = true
        }
        case _ => {
          os.println("invalid input. try again!")
          response = is.readLine()
        }
      }

    //    log on to existing user
  }

  @tailrec private def startGame(luser: User) {
    val response = luser.is.readLine().trim
    if (response != "quit") {
      if (cases.size * 3 < users.size) startCase(luser)
      processCmd(luser, response)
      startGame(luser)
    } else {
      luser.os.println("save game? y/n")
      if (luser.is.readLine.trim == "y") {
        println(luser.number)
        usersRecord.update(luser.number, luser.id)
        //        usersRecord.close()
      }
      luser.sock.close()
    }
  }

  @tailrec private def checkNameAvailability(is: BufferedReader, os: PrintStream): String = {
    val name = is.readLine()
    var nameTaken = false
    for (i <- 0 until users.size) {
      if (users.contains(name)) nameTaken = true
    }
    if (nameTaken) {
      os.println("This name has been taken. Choose another one.")
      os.flush()
      checkNameAvailability(is, os)
    } else name
  }

  @tailrec private def checkQuit {
    if (readLine() != ":quit") checkQuit
  }
}