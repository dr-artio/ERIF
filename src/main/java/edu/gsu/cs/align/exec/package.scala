package edu.gsu.cs.align

import java.io.File
import com.simontuffs.onejar.Boot
import net.sf.samtools.SAMRecord
import org.biojava3.core.sequence.DNASequence
import edu.gsu.cs.align.io.{SAMParser, FASTAParser}

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
  val READS_FILE = "reads.sam"
  var output_folder: File = null
  var reads: Iterable[SAMRecord] = null
  var ref: DNASequence = null

  def runInDelFixer(args: Array[String]) = {
    try {
      Boot.run(args)
    } catch {
      case e: Exception => {
        System.err.println(e.getMessage)
        System.exit(-1)
      }
    }
    val o = args.indexOf(OUTPUT_PARAMETER)
    if (o == -1) output_folder = new File(System.getProperty(USER_DIR))
    else output_folder = new File(args(o + 1))

    output_folder.getAbsolutePath + File.separator + READS_FILE
  }

  def initReadsAdnReference(path_to_ref: String, path_to_sam: String) = {
    ref = FASTAParser.readReference(path_to_ref)
    reads = SAMParser.readSAMFile(path_to_sam)
  }
}
