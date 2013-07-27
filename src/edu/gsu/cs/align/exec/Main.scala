package edu.gsu.cs.align.exec

import edu.gsu.cs.align.io.{MSAWriter, FASTAParser, SAMParser}
import java.io.FileNotFoundException
import edu.gsu.cs.align.model.{InsertionsAligner, InsertionsHandler}


/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 6/27/13
 * Time: 12:39 PM
 * Main object to run alignment extension based on
 * aligned reads (SAM file) of fasta reads via
 * alignment by InDelFixer [Topfer et. al.]
 */
object Main {
  /**
   * Main entry point. Redirect user to InDelFixer interface
   * or handle its own if reads already aligned.
   * @param args
   *             list of program arguments
   */
  def main(args: Array[String]): Unit = {
    try {
      val (path_to_ref, path_to_sam) = parseArgs(args)
      initReadsAdnReference(path_to_ref, path_to_sam)
      //val n = reads.map(r => r.getAlignmentStart + r.getReadLength).max
      InsertionsHandler.buildInsertionTable(reads, ref.getLength + 1)
      val exRef = InsertionsHandler.getExtendedReference(ref.getSequenceAsString)
      val path_to_ext_ref = path_to_ref + "_ext.fasta"
      FASTAParser.writeAsFASTA(exRef, path_to_ext_ref)
      val ext_len = exRef.length
      InsertionsAligner.buildAndInitInsertionsTable(reads, ext_len)
      InsertionsAligner.performInsertionsAlignment
      val e_r = reads.map(r => InsertionsAligner.transformRead(r, ext_len))
      MSAWriter.writeExtendedReadsInInternalFormat(path_to_sam + "_ext.txt",
        e_r)

    } catch {
      case e: FileNotFoundException => {
        System.err.println(e.getMessage)
      }
      case e: IndexOutOfBoundsException => {
        System.err.println(e.getMessage)
        e.printStackTrace
      }
      case e: Exception => e.printStackTrace
    } finally {
        System.exit(0)
    }
  }

  private def parseArgs(args: Array[String]) = {
    val sam = args.indexOf(ALIGNED_READS_PARAMETER)
    val g = args.indexOf(REFERENCE_PARAMETER)
    var path_to_sam = ""
    path_to_sam = if (sam == -1) runInDelFixer(args) else args(sam + 1)
    if (g == -1) {
      System.err.println("Reference file is not specified! Use -g <path_to_ref>")
      System.exit(-1)
    }
    val path_to_ref = args(g + 1)
    (path_to_ref, path_to_sam)
  }
}
