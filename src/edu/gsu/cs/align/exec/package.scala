package edu.gsu.cs.align

import java.io.File
import ch.ethz.bsse.indelfixer.minimal.Start
import net.sf.samtools.{CigarOperator, SAMRecord}
import org.biojava3.core.sequence.DNASequence
import edu.gsu.cs.align.io.{SAMParser, FASTAParser}
import ch.ethz.bsse.indelfixer.utils.StatusUpdate
import scala.collection.JavaConversions._
import edu.gsu.cs.align.model.WindowSlicer
import edu.gsu.cs.align.io.SAMParser.getSAMFileHeader

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/11/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
package object exec {
  val USER_DIR = "user.dir"
  val OUTPUT_PARAMETER = "-o"
  val INPUT_PARAMETER = "-i"
  val REFERENCE_PARAMETER = "-g"
  val ALIGNED_READS_PARAMETER = "-sam"
  val INTERVAL_PARAMETER = "-r"
  val READS_FILE = "reads.sam"
  var output_folder: File = null
  var reads: List[SAMRecord] = null
  var ref: DNASequence = null
  var start = 0
  var end = -1

  def getOutputDirPath = {
    if (!(output_folder.exists && output_folder.isDirectory)) {
      output_folder.mkdir
    }
    output_folder.getAbsolutePath
  }

  def runInDelFixer(args: Array[String]) = {
    try {
      Start.main(args)
      ResetStatusCounts
    } catch {
      case e: Exception => {
        System.err.println(e.getMessage)
        System.exit(-1)
      }
    }

    output_folder.getAbsolutePath + File.separator + READS_FILE
  }

  def initReadsAdnReference(path_to_ref: String, path_to_sam: String) = {
    ref = FASTAParser.readReference(path_to_ref)
    reads = SAMParser.readSAMFile(path_to_sam)
    println("\nUnfiltered reads:" + reads.size)
    reads = reads.filter(r => {
     val tmp = r.getCigar.getCigarElements.filter(c => c.getOperator == CigarOperator.I).map(_.getLength)
     tmp.isEmpty || tmp.max < 25
    })
    if (end != -1) {
      val w = WindowSlicer.cutWindowFromGlobalAlignment(start, end - start, reads, getSAMFileHeader(path_to_sam))
      reads = w.getReads
    }
    println("Filtered reads:" + reads.size)
  }

  private def ResetStatusCounts {
    StatusUpdate.readCount = 0
    StatusUpdate.alignCount1 = 0
    StatusUpdate.alignCount2 = 0
    StatusUpdate.alignCount3 = 0
    StatusUpdate.tooSmallCount = 0
    StatusUpdate.unmappedCount = 0
  }
}
