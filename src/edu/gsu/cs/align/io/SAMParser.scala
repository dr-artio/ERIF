package edu.gsu.cs.align.io

import java.io.File
import net.sf.samtools.{SAMFileHeader, SAMFileReader, SAMRecord}
import scala.collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 6/27/13
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
object SAMParser {

  def readSAMFile(p: String): List[SAMRecord] = {
    val file = new File(p)
    readSAMFile(file)
  }

  def readSAMFile(f: File): List[SAMRecord] = {
    val reader = checkFileAndGetReader(f)
    var res = new ListBuffer[SAMRecord]()
    val iter = reader.iterator
    while (iter.hasNext) res += iter.next
    res.toList
  }

  def checkFileAndGetReader(f: File) = {
    if (!f.exists()) {
      System.err.println("File not found!")
      System.exit(-1)
    }
    new SAMFileReader(f)
  }

  def getSAMFileHeader(f: File): SAMFileHeader = {
    val reader = checkFileAndGetReader(f)
    reader.getFileHeader
  }

  def getSAMFileHeader(path: String): SAMFileHeader = {
    val file = new File(path)
    getSAMFileHeader(file)
  }
}
