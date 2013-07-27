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
  val read1 = "RRead1X\t0\tTestConsensus\t2\t255\t1M2I3M1D1I1M\t*\t0\t0\tAACTAGCT\t*"
  val read2 = "RRead2X\t0\tTestConsensus\t2\t255\t1M1I2M1D1M2I1M1I1M\t*\t0\t0\tACTACGCTAT\t*"
  val cons =  "TestConsensus"
  val cons_str = "TATAGCTT"

  test("Read transformer works correctly") {
    val header = new SAMFileHeader()
    header.addSequence(new SAMSequenceRecord(cons, cons_str.length))
    val parser = new SAMLineParser(header)
    val read1 = parser.parseLine(this.read1)
    val read2 = parser.parseLine(this.read2)
    InsertionsHandler.buildInsertionTable(List(read1, read2), cons_str.length+1)
    val extRef = InsertionsHandler.getExtendedReference(cons_str)
    InsertionsAligner.buildAndInitInsertionsTable(List(read1, read2), extRef.length)
    InsertionsAligner.performInsertionsAlignment
    println(extRef + ".")
    val extead2 = InsertionsAligner.transformRead(read2, 13)
    println(extead2 + ".")
    val extread1 = InsertionsAligner.transformRead(read1, 13)
    println(extread1 + ".")
  }
}
