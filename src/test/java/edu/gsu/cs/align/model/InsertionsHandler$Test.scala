package edu.gsu.cs.align.model

import org.scalatest.FunSuite
import net.sf.samtools._

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/18/13
 * Time: 1:37 PM
 * To change this template use File | Settings | File Templates.
 */
class InsertionsHandler$Test extends FunSuite {
   test("Insertion table built appropriately") {
     val header = new SAMFileHeader()
     header.addSequence(new SAMSequenceRecord("TestConsensus",6))
     val parser = new SAMLineParser(header)
     val read1 = parser.parseLine("RRead1X\t0\tTestConsensus\t1\t255\t1M2I2M1D2M\t*\t0\t0\tAACTAGT\t*")
     val read2 = parser.parseLine("RRead2X\t0\tTestConsensus\t1\t255\t1M1I2M1D2M\t*\t0\t0\tACTAGT\t*")
     InsertionsHandler.buildInsertionTable(List(read1,read2), 6)
     assert(InsertionsHandler.extInserts(0) == 0)
     assert(InsertionsHandler.extInserts(1) == 2)
     assert(InsertionsHandler.insertions(1).length == 2)
   }
}
