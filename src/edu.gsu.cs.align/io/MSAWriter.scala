package edu.gsu.cs.align.io

import java.io.{PrintWriter, File}

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/25/13
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
object MSAWriter {

  /**
   * Override for writeExtendedReadsInInternalFormat method
   * with path to file instead of {@see File} object
   * @param file
   *             Path to file
   * @param ext_reads
   *                  Collection of extended reads
   */
  def writeExtendedReadsInInternalFormat(file: String, ext_reads: Iterable[String]): Unit = {
    val fl = new File(file)
    fl.createNewFile()
    if (!fl.canWrite) {
      System.err.println("File couldn't be written!")
      return
    }
    writeExtendedReadsInInternalFormat(fl, ext_reads)
  }

  /**
   * Write extended reads in a simple internal format.
   * Global align with spaces and inner gaps with dashes.
   * @param file
   *             File object to write reads.
   * @param ext_reads
   *                  Collection of extended reads
   */
  def writeExtendedReadsInInternalFormat(file: File, ext_reads: Iterable[String]) = {
    var writer:PrintWriter = null
    try {
      writer = new PrintWriter(file)
      for (read <- ext_reads) writer.write(read + '\n')
    }
    catch {
      case e: Exception => {
        System.err.println(e.getMessage)
      }
    }
    finally {
      writer.close
    }
  }
}
