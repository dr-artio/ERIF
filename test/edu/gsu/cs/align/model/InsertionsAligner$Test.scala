package edu.gsu.cs.align.model

//import edu.gsu.cs.align.model.{InsertionsAligner, InsertionsHandler}
import org.scalatest.FunSuite
import net.sf.samtools.{SAMLineParser, SAMSequenceRecord, SAMFileHeader}

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/25/13
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
class InsertionsAligner$Test extends FunSuite {
  val read1 = "RRead1X\t0\tTestConsensus\t1\t255\t1M2I3M1D1M\t*\t0\t0\tAACTAGT\t*"
  val read2 = "RRead2X\t0\tTestConsensus\t1\t255\t1M1I2M1D2M\t*\t0\t0\tACTACT\t*"
  val cons =  "TestConsensus"
  val cons_str = "TATAGCT"

  test("Read transformer works correctly") {
    val header = new SAMFileHeader()
    header.addSequence(new SAMSequenceRecord(cons,6))
    val parser = new SAMLineParser(header)
    val read1 = parser.parseLine(this.read1)
    val read2 = parser.parseLine(this.read2)
    InsertionsHandler.buildInsertionTable(List(read1, read2), 8)
    val extRef = InsertionsHandler.getExtendedReference(cons_str)
    InsertionsAligner.buildAndInitInsertionsTable(List(read1, read2), 8)
    InsertionsAligner.performInsertionsAlignment
    println(extRef)
    val extead2 = InsertionsAligner.transformRead(read2, 9)
    println(extead2 + ".")
    val extread1 = InsertionsAligner.transformRead(read1, 9)
    println(extread1 + ".")
  }
}
