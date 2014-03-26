package edu.gsu.cs.align.model

import net.sf.samtools.{SAMFileHeader, SAMTextWriter, SAMRecord}
import java.io.File

/**
 */
class Window(globalStart: Int, width: Int, reads: List[SAMRecord], header: SAMFileHeader) {
  var filename: String = "window.sam"

  def getDefaultFolderName = {
    "st_%d_w%d".format(globalStart, width)
  }

  def getInterval = {
    "%d-%d".format(globalStart, globalStart + width)
  }

  def setFileName(path: String) = {
    filename = "%s_st%d_w%d.sam".format(path, globalStart, width)
  }

  def writeWindow = {
    val file = new File(filename)
    file.createNewFile()
    val wr = new SAMTextWriter(file)
    wr.setHeader(header)
    reads.foreach(wr.addAlignment)
    wr.finish()
    wr.close()
  }

  def getReads = reads
}
