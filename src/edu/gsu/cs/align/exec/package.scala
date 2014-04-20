package edu.gsu.cs.align

import java.io.{FileWriter, File}
import ch.ethz.bsse.indelfixer.minimal.Start
import net.sf.samtools.{SAMFileHeader, CigarOperator, SAMRecord}
import org.biojava3.core.sequence.DNASequence
import edu.gsu.cs.align.io.{SAMParser, FASTAParser}
import ch.ethz.bsse.indelfixer.utils.StatusUpdate
import scala.collection.JavaConversions._
import edu.gsu.cs.align.model.{InsertionsAligner, InsertionsHandler, WindowSlicer}
import edu.gsu.cs.align.io.SAMParser.getSAMFileHeader
import java.util.Date
import java.text.SimpleDateFormat
import org.biojava3.core.sequence.io.FastaWriterHelper

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/11/13
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
package object exec {
  val LINE = "--------------------------------------------"
  val ERIF = "ERIF v %s Extended Reference Insertion Fixer"
  val USER_DIR = "user.dir"
  val OUTPUT_PARAMETER = "-o"
  val INPUT_PARAMETER = "-i"
  val REFERENCE_PARAMETER = "-g"
  val ALIGNED_READS_PARAMETER = "-sam"
  val INTERVAL_PARAMETER = "-r"
  val READS_FILE = "reads.sam"
  val sdf = new SimpleDateFormat("[hh:mm:ss a]");
  var output_folder: File = null
  var reads: List[SAMRecord] = null
  var ref: DNASequence = null
  var start = 0
  var end = -1

  private def getOutputDirPath = {
    if (!(output_folder.exists && output_folder.isDirectory)) {
      output_folder.mkdir
    }
    output_folder.getAbsolutePath
  }

  private def runInDelFixer(args: Array[String]) = {
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

  protected[exec] def initReadsAdnReference(path_to_ref: String, path_to_sam: String) = {
    ref = FASTAParser.readReference(path_to_ref)
    reads = SAMParser.readSAMFile(path_to_sam)
    reads = filterAndSliceRecords(reads, getSAMFileHeader(path_to_sam), start, end)
  }

  def filterAndSliceRecords(reads: List[SAMRecord], header: SAMFileHeader, start: Int, end: Int) = {
    log("Total reads:" + reads.size)
    log("Filtering...")
    var fReads = reads.filter(r => {
      val tmp = r.getCigar.getCigarElements.view.filter(c => c.getOperator == CigarOperator.I).map(_.getLength)
      r.getReadLength > 2000 && (tmp.isEmpty || tmp.max < 10)
    })
    if (end != -1) {
      val w = WindowSlicer.cutWindowFromGlobalAlignment(start, end - start, fReads, header)
      fReads = w.getReads
    }
    log("Filtered reads:" + fReads.size)
    fReads
  }

  def performAlignment(reads: List[SAMRecord] = reads, ref: DNASequence = ref, start: Int = start, end: Int = end) = {
    InsertionsHandler.buildInsertionTable(reads, ref.getLength + 1)
    val exRef = InsertionsHandler.getExtendedReference(ref.getSequenceAsString)
    //val path_to_ext_ref = path_to_ref + "_ext.fasta"
    //FASTAParser.writeAsFASTA(exRef, path_to_ext_ref)
    val ext_len = if (end == -1) exRef.length else end + exRef.length - ref.getLength
    InsertionsAligner.buildAndInitInsertionsTable(reads, ext_len + 1)
    log("Table built, ext_len: %d".format(ext_len))
    log("Cleaning up insertions table...")
    val (count, e_len) = InsertionsAligner.cleanUpInsertionTable(-1, ext_len)
    log("Entries dropped: %d new length: %d".format(count, e_len))
    log("Cleaned!")

    InsertionsAligner.performInsertionsAlignment
    log("Insertions aligned")

    reads.map(r => {
      val seq = InsertionsAligner.transformRead(r, ref.getLength + InsertionsHandler.extInserts.sum, start)
      val record = new DNASequence(seq)
      record.setOriginalHeader(r.getReadName)
      record
    })
  }
  
  protected[exec] def writeResult(aligned_reads: List[DNASequence]) {
    //MSAWriter.writeExtendedReadsInInternalFormat(path_to_sam + "_ext.txt",e_r)
    val fl = getOutputDirPath + File.separator + "aligned_reads.fas"
    val fw = new FileWriter(fl, false)
    FastaWriterHelper.writeNucleotideSequence(new File(fl), aligned_reads)
    fw.close()
  }

  protected[exec] def parseArgs(args: Array[String]) = {
    val sam = args.indexOf(ALIGNED_READS_PARAMETER)
    val g = args.indexOf(REFERENCE_PARAMETER)
    val o = args.indexOf(OUTPUT_PARAMETER)
    val r = args.indexOf(INTERVAL_PARAMETER)
    if (o != -1 && args.length > o + 1 && !args(o+1).endsWith(File.separator)) {
      args(o+1) = args(o+1) + File.separator
    }
    if (o == -1) output_folder = new File(System.getProperty(USER_DIR))
    else output_folder = new File(args(o + 1))
    var path_to_sam = ""
    path_to_sam = if (sam == -1) runInDelFixer(args) else args(sam + 1)
    if (g == -1) {
      System.err.println("Reference file is not specified! Use -g <path_to_ref>")
      System.exit(-1)
    }
    if (r != -1) {
      val tmp = args(r+1).split("-")
      start = tmp(0).toInt
      end = tmp(1).toInt
    }
    val path_to_ref = args(g + 1)
    (path_to_ref, path_to_sam)
  }

  protected[exec] def printGreetings = {
    log(LINE)
    log(ERIF.format(Main.getClass.getPackage.getImplementationVersion))
    log(LINE)
  }

  protected[exec] def log(mes: String) = {
    val date = new Date()
    println("%s %s".format(sdf.format(date), mes))
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
