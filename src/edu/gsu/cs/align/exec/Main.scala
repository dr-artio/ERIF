package edu.gsu.cs.align.exec

import java.io.FileNotFoundException
import org.biojava3.core.sequence.DNASequence


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
      printGreetings
      // parse arguments
      val (path_to_ref, path_to_sam) = parseArgs(args)

      // init (read) data from input files
      initReadsAdnReference(path_to_ref, path_to_sam)

      // Perform insertion alignment (fixing)
      log("Alignment started...")
      val aligned_reads: List[DNASequence] = performAlignment()

      // write result to output folder
      writeResult(aligned_reads)
      log("Finished!")

    } catch {
      case e: FileNotFoundException => {
        System.err.println(e.getMessage)
      }
      case e: IndexOutOfBoundsException => {
        System.err.println(e.getMessage)
        e.printStackTrace
      }
      case e: Exception => e.printStackTrace
    }
  }
}
