package edu.gsu.cs.align.model

import net.sf.samtools.{CigarOperator, SAMRecord}
import collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/2/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
object InsertionsHandler {
  var insertions: IndexedSeq[ListBuffer[SAMRecord]] = null
  var extInserts: Array[Int] = null
  var extInsertsSecond: Array[Int] = null

  def buildInsertionTable(reads: Iterable[SAMRecord], n: Int) = {
    insertions = (1 to n).map(x => new ListBuffer[SAMRecord])
    extInserts = new Array[Int](n)
    extInsertsSecond = new Array[Int](n)
    for (read <- reads){
      var pos = read.getAlignmentStart - 1
      for (ce <- read.getCigar.getCigarElements) {
        if (ce.getOperator != CigarOperator.I) {
          pos += ce.getLength
        } else {
          insertions(pos) += read
          extInsertsSecond(pos) = extInserts(pos)
          if (extInserts(pos) < ce.getLength) extInserts(pos) = ce.getLength
        }
      }
    }
  }

  def getExtendedReference(ref: String) = {
    val length = ref.length
    val sb = new StringBuilder(2 * length)
    println(extInsertsSecond.max)
    for (i <- 0 until length) {
      for (s <- 0 until extInsertsSecond(i)) sb += '-'
      sb += ref(i)
    }
    sb.toString
  }
}
