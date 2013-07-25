package edu.gsu.cs.align.model

//import edu.gsu.cs.align.model.InsertionsHandler
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
  val read1 = "RRead1X\t0\tTestConsensus\t1\t255\t1M2I2M1D2M\t*\t0\t0\tAACTAGT\t*"
  val read2 = "RRead2X\t0\tTestConsensus\t1\t255\t1M1I2M1D2M\t*\t0\t0\tACTAGT\t*"
  val cons =  "TestConsensus"
  val cons_str = "ATAGT"

  test("Insertion table built appropriately") {
    val header = new SAMFileHeader()
    header.addSequence(new SAMSequenceRecord(cons, 6))
    val parser = new SAMLineParser(header)
    val read1 = parser.parseLine(this.read1)
    val read2 = parser.parseLine(this.read2)
    InsertionsHandler.buildInsertionTable(List(read1, read2), 6)
    assert(InsertionsHandler.extInserts(0) == 0)
    assert(InsertionsHandler.extInserts(1) == 2)
    assert(InsertionsHandler.insertions(1).length == 2)
  }

  test("Extended reference") {
    val header = new SAMFileHeader()
    header.addSequence(new SAMSequenceRecord(cons, 6))
    val parser = new SAMLineParser(header)
    val read1 = parser.parseLine(this.read1)
    val read2 = parser.parseLine(this.read2)
    InsertionsHandler.buildInsertionTable(List(read1, read2), 6)
    val eRef = InsertionsHandler.getExtendedReference(cons_str)
    println(eRef)
    assert(eRef.length == 7)
  }
}
