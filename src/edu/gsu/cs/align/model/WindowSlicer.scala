package edu.gsu.cs.align.model

import net.sf.samtools._
import scala.collection.JavaConversions._

/**

  */
object WindowSlicer {
  val TAB = "\t"
  val ASTERISK = "*"

  def cutWindowFromGlobalAlignment(start: Int, width: Int, alignment: List[SAMRecord], header: SAMFileHeader) = {
    val wReads = alignment.filter(s => isOverlap(start, width, s))
    new Window(start, width, wReads.map(truncateRead(start, width, _)), header)
  }

  private def isOverlap(start: Int, width: Int, read: SAMRecord) = {
    if (read.getAlignmentEnd < start + 0.1*width || read.getAlignmentStart >= start + 0.9*width)
      false
    else
      true
  }

  /**
   * Truncate aligned read in SAM format
   * and return {@see SAMRecord} object
   * with cut read.
   * @param start
   *              global starting index of window (1-based)
   * @param width
   *              The width of the window
   * @param read
   *             Original {@see SAMRecord} read
   * @return
   *         Cut {@see SAMRecord} read
   */
  private def truncateRead(start: Int, width: Int, read: SAMRecord) = {

    val slicedReadStr = new StringBuffer()
    val readString = read.getReadString
    val qualityString = read.getBaseQualityString
    val isQualityStringGiven = !qualityString.equals(ASTERISK)
    val cigar = new Cigar()
    val qualStr = new StringBuffer()

    var slicingIndex = read.getAlignmentStart - 1

    val newAlignmentStart = List(slicingIndex, start).max

    var i = 0
    for (c <- read.getCigar.getCigarElements) {
      var elemLen = 0
      for (n <- 0 until c.getLength) {
        if (slicingIndex >= start && slicingIndex < start + width) {
          elemLen += 1
          if (c.getOperator.consumesReadBases) {
              val nucl = readString.charAt(i)
              val qualChar = if (isQualityStringGiven) qualityString.charAt(i) else ""
              slicedReadStr.append(nucl)
              qualStr.append(qualChar)
          }
        }
        if (c.getOperator.consumesReferenceBases) {
          slicingIndex += 1
        }
        if (c.getOperator.consumesReadBases) {
          i += 1
        }
      }
      if (elemLen > 0) {
        val ce = new CigarElement(elemLen, c.getOperator)
        cigar.add(ce)
      }
    }
    if (!isQualityStringGiven) qualStr.append(ASTERISK)
    val s = buildCutSAMRecord(read, cigar, slicedReadStr.toString, newAlignmentStart + 1, qualStr.toString)
    s
  }

  private def buildCutSAMRecord(read: SAMRecord, cigar: Cigar, readString: String, alignmentStart: Int, qualStr: String) = {
    // get pure string in SAM format
    val samStrs = read.getSAMString.split(TAB).iterator
    // To build new string in SAM format
    val slicedSAMStr = new StringBuffer()
    // 1st token is read name. Leave it as it is
    slicedSAMStr.append(samStrs.next() + TAB)
    // 2nd token FLAG. Leave it as it is
    slicedSAMStr.append(samStrs.next() + TAB)
    // 3rd token Reference name. Leave it as it is
    slicedSAMStr.append(samStrs.next() + TAB)
    // 4th token AlignmentStart. Update according to cut
    slicedSAMStr.append(alignmentStart + TAB)
    samStrs.next()
    // 5th token MAPQ score. Leave it as it is
    slicedSAMStr.append(samStrs.next() + TAB)
    // 6th token CigarString. Update with new value
    slicedSAMStr.append(cigar + TAB)
    samStrs.next()
    // 7th token Ref. name of the mate/next read. Leave it as it is
    slicedSAMStr.append(samStrs.next() + TAB)
    // 8th token Position of the mate/next read. Leave it as it is
    slicedSAMStr.append(samStrs.next() + TAB)
    // 9th token observed Template LENgth. Leave it as it is
    slicedSAMStr.append(samStrs.next() + TAB)
    // 10th token segment SEQuence. Update according to cut
    slicedSAMStr.append(readString + TAB)
    // 11th token ASCII of Phred-scaled base QUALity+33. Update according to cut
    slicedSAMStr.append(qualStr + TAB)
    samStrs.next()
    // Everything else add as it is
    // TODO: Take care about tail
    //while (samStrs.hasNext) slicedSAMStr.append(samStrs.next() + TAB)

    val parser = new SAMLineParser(read.getHeader)
    parser.parseLine(slicedSAMStr.toString.stripMargin)
  }
}
