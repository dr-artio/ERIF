package edu.gsu.cs.align.exec

import edu.gsu.cs.align.io.SAMParser
import java.io.FileNotFoundException


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
  def main(args: Array[String]) = {
    try {
      val sam = args.indexOf(ALIGNED_READS_PARAMETER)
      var path_to_sam = ""
      path_to_sam = if (sam == -1) runInDelFixer(args) else args(sam + 1)

      val reads = SAMParser.readSAMFile(path_to_sam)

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
