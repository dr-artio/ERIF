package edu.gsu.cs.align.model

import net.sf.samtools.SAMRecord
import collection.JavaConversions._

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/2/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
object InsertionsHandler {
  def buildInsertionTable(reads: Iterable[SAMRecord], n: Int) = {
    val insertions = new Array[List[SAMRecord]](n)
    for (read <- reads){
      var pos = read.getAlignmentStart
      for (ce <- read.getCigar.getCigarElements) {

      }
    }
  }
}
