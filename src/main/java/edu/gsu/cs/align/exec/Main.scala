package edu.gsu.cs.align.exec

import edu.gsu.cs.align.io.{FASTAParser, SAMParser}
import java.io.FileNotFoundException
import edu.gsu.cs.align.model.InsertionsHandler


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
      val sam = args.indexOf(ALIGNED_READS_PARAMETER)
      val g = args.indexOf(REFERENCE_PARAMETER)
      var path_to_sam = ""
      path_to_sam = if (sam == -1) runInDelFixer(args) else args(sam + 1)
      if (g == -1) {
        System.err.println("Reference file is not specified! Use -g <path_to_ref>")
        System.exit(-1)
      }
      val path_to_ref = args(g + 1)
      val ref = FASTAParser.readReference(path_to_ref)

      val reads = SAMParser.readSAMFile(path_to_sam)
      val n = reads.map(r => r.getAlignmentStart + r.getReadLength).max
      InsertionsHandler.buildInsertionTable(reads, n)
      val exRef = InsertionsHandler.getExtendedReference(ref.getSequenceAsString)
      val path_to_ext_ref = path_to_ref + "_ext.fasta"
      FASTAParser.writeAsFASTA(exRef, path_to_ext_ref)
      args(g + 1) = path_to_ext_ref
      runInDelFixer(args)

    } catch {
      case e: FileNotFoundException => {
        System.err.println(e.getMessage)
      }
      case e: IndexOutOfBoundsException => {
        System.err.println("Wrong arguments! Use either -sam PATH to bypass alignment and send SAM file directly")
        System.err.println("                 or set or parameters for InDelFixer")
      }
    } finally {
        System.exit(0)
    }
  }
}
