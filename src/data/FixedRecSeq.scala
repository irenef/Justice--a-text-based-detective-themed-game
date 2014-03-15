package data

import game._
import game.Item._
import data.GameMap._
import data.Main._
import java.io.{ File, DataOutput, DataInput, RandomAccessFile }
import scala.collection.mutable
import scala.collection.mutable.Buffer

class FixedRecordSeq[A](file: File, recordLen: Int, reader: DataInput => A, writer: (DataOutput, A) => Unit) extends mutable.IndexedSeq[A] {
  private val raf = new RandomAccessFile(file, "rw")

  def apply(index: Int): A = {
    raf.seek(index * recordLen)
    reader(raf)
  }

  def update(index: Int, a: A) {
    raf.seek(index * recordLen)
    writer(raf, a)
  }

  def length: Int = (raf.length / recordLen).toInt

  def close() = raf.close()
}

object FixedRecordSeq {
  val usersRecord = new FixedRecordSeq[Detective](new File(usersRecFile), 20 + 20 + 8 + 4 * 5 + 40, readDet, writeDet)
  //name: 10chars, pw: 10chars, exp: 1double, inv:5ints, place:10chars) 
  private def readDet(din: DataInput): Detective = {
    val bufStrOfLen10 = new Array[Byte](20)
    val bufStrOfLen20 = new Array[Byte](40)
    din.readFully(bufStrOfLen10)
    val name = new String(bufStrOfLen10.takeWhile(_ > 0))
    din.readFully(bufStrOfLen10)
    val pw = new String(bufStrOfLen10.takeWhile(_ > 0))
    val exp = din.readDouble()
    val inv: List[String] = List()
    for (i <- 0 until 5) (allItems(din.readInt())) :: inv
    //    (Array.fill(5)(allItems(din.readInt()))).toBuffer
    //    TODO - replace the "5"
    din.readFully(bufStrOfLen20)
    val place = new String(bufStrOfLen20).takeWhile(_ > 0)
    new Detective(name, pw, exp, inv, origMap(place))
  }

  private def writeDet(dout: DataOutput, d: Detective) {
    dout.write(d.name.take(20).getBytes.padTo(20, 0.toByte))
    dout.write(d.getPW.take(20).getBytes.padTo(20, 0.toByte))
    dout.writeDouble(d.getEXP)
    for (i <- 0 until d.inv.length) dout.writeInt(allItems.indexOf(d.inv(i)))
    //  if items are saved as strings:  
    //    for(i <- 0 until d.inv.length) dout.write(d.inv(i).take(20).getBytes.padTo(20, 0.toByte))
    dout.write(d.l.name.take(40).getBytes.padTo(40, 0.toByte))
  }
}