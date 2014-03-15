package networking

import data.Main._
import java.net.Socket
import java.io._
import scala.annotation.tailrec
import actors.Actor.actor

object Client {
    val sock = new Socket("localhost", port)
    val dis = new BufferedReader(new InputStreamReader(sock.getInputStream()))
    val dos = new PrintStream(sock.getOutputStream())
    val name = readLine
    dos.println(name)
    dos.flush
    val response = null
    if (response != ":quit") {
      actor { incoming(dis) }
      outgoing(dos)
    } else { 
      dos.println("the server has rejected you :(")
      dos.close}
    sock.close
    sys.exit(0)

  @tailrec private def incoming(dis: BufferedReader) {
    val line = dis.readLine()
    incoming(dis)
  }

  @tailrec private def outgoing(dos: PrintStream) {
    val input = readLine().trim
    dos.println(input)
    dos.flush
    if (input != ":quit") outgoing(dos)
  }
}