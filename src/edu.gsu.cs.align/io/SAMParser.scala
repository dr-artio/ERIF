package edu.gsu.cs.align.io

import java.io.File
import net.sf.samtools.{SAMFileReader, SAMRecord}
import scala.collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 6/27/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
object SAMParser {

  def readSAMFile(p: String): Iterable[SAMRecord] = {
    val file = new File(p)
    readSAMFile(file)
  }

  def readSAMFile(f: File): Iterable[SAMRecord] = {
    if (!f.exists()) {
      System.err.println("File not found!")
      System.exit(-1)
    }
    val reader = new SAMFileReader(f)
    var res = new ListBuffer[SAMRecord]()
    val iter = reader.iterator
    while (iter.hasNext) res += iter.next
    res.toList
  }
}
