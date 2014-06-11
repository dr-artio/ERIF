package edu.gsu.cs.align.model

import scala.collection.mutable.HashMap
import net.sf.samtools.SAMRecord
import collection.JavaConversions._

import edu.gsu.cs.align.model.InsertionsHandler.extInserts

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/23/13
 * Time: 3:43 PM
 */
object InsertionsAligner {
  private var insertionsTable: Array[Map[SAMRecord, String]] = null
  private val S = "N"
  private val DASH = "-"

  def transformRead(read: SAMRecord, ext_len: Int, start: Int = 0) = {
    val sb = new StringBuilder
    val readSeq = read.getReadString
    var ext_index = 0
    var index = read.getAlignmentStart - 1
    for (i <- start until index) {
      ext_index += edu.gsu.cs.align.model.InsertionsHandler.extInserts(i) + 1
    }
    sb ++= S * ext_index
    var i = 0
    for (c <- read.getCigar.getCigarElements) {
      if (c.getOperator.consumesReferenceBases) {
        for (j <- 0 until c.getLength) {
          sb ++= (if (c.getOperator.consumesReadBases) {i += 1;readSeq(i - 1).toString} else DASH)

          if (insertionsTable(index).contains(read)) {
            //sb ++= insertionsTable(index)(read)
          } else {
            sb ++= (DASH * extInserts(index))
          }
          index += 1
        }
      } else {
        if (insertionsTable(index) contains read) {
          val ins = insertionsTable(index)(read)
          sb ++= ins
        }
        i += c.getLength
      }
    }
    sb ++= S * (ext_len - sb.toString().length - start)
    sb.toString()
  }

  private def getInsertionOrPadding(index: Int, read: SAMRecord): String = {
    if (insertionsTable(index) contains read) {
      insertionsTable(index)(read)
    } else {
      DASH * extInserts(index)
    }
  }

  /**
   * Perform alignment of inserted regions,
   * store aligned regions in the insertions
   * table.
   */
  def performInsertionsAlignment() = {
    val len = insertionsTable.length
    for (i <- (0 until len).par) {
      val map = insertionsTable(i)
      if (map.nonEmpty) {
        insertionsTable(i) = alignInsertedRegion(map)
      }
    }
  }

  /**
   * Build insertions table for aligning them separately
   * mapped to {@see SAMRecord} objects.
   * Prepare step for MSA heuristic
   * @param reads
   * Aligned reads (InDelFixer)
   * @param cons_len
   * Length of the consensus (original)
   */
  def buildAndInitInsertionsTable(reads: Iterable[SAMRecord], cons_len: Int) = {
    val tmpTable = new Array[HashMap[SAMRecord, String]](cons_len)
    (0 until cons_len).foreach(i => tmpTable(i) = HashMap.empty[SAMRecord, String])

    for (r <- reads) {
      var i = r.getAlignmentStart - 1
      var j = 0
      for (c <- r.getCigar.getCigarElements) {
        if (!c.getOperator.consumesReferenceBases) {
          tmpTable(i) += (r -> r.getReadString.substring(j, j + c.getLength))
        } else {
          i += c.getLength
        }
        if (c.getOperator.consumesReadBases) j += c.getLength
      }
    }
    insertionsTable = tmpTable.map(l => l.toMap)
  }

  def cleanUpInsertionTable(thr: Int, ln: Int) = {
    val len = insertionsTable.length
    var count = 0
    var l = ln
    for (i <- 0 until len) {
      if (insertionsTable(i).size <= thr) {
        if (insertionsTable(i).nonEmpty) l -= InsertionsHandler.extInserts(i)
        insertionsTable(i) = Map[SAMRecord, String]()
        if (i < InsertionsHandler.extInserts.length) InsertionsHandler.extInserts(i) = 0
        count += 1
      }
    }
    (count, l + 1)
  }

  /**
   * Align inserted region sequences.
   * Regions come from global alignment.
   * @param seqs
   * Short inserted fragments
   * @return
   * Aligned fragments
   */
  private def alignInsertedRegion(seqs: Map[SAMRecord, String]) = {
    val result = HashMap.empty[SAMRecord, String]
    val cons = buildConsensus(seqs.values)
    val maxl = cons.data.length
    val seqs_to_align = seqs.filter(s => s._2.length < maxl)
    result ++= seqs.filter(s => s._2.length == maxl)
    result ++= seqs_to_align.map(s => s._1 -> findBestScoreAndExtend(s._2, cons))
    result.toMap
  }

  private def findBestScoreAndExtend(str: String, cons: FractionalString) = {
    val n = cons.data.length
    val k = n - str.length
    val variants = generateAllKSubsets(n, k)
    var bestScore = -1D
    var best: IndexedSeq[Int] = null
    for (gaps <- variants) {
      var i = 0
      var j = 0
      var score = 1.0
      while (i < n) {
        if (!gaps.contains(i)) {
          val dt = cons.data(i)
          if (dt.contains(str(j))) score *= dt(str(j))
          j += 1
        }
        i += 1
      }
      if (score > bestScore) {
        bestScore = score
        best = gaps
      }
    }
    getGappedSeq(n, best, str)
  }


  private def getGappedSeq(n: Int, best: IndexedSeq[Int], str: String): String = {
    val sb = new StringBuilder
    var i = 0
    var j = 0
    while (i < n) {
      if (!best.contains(i)) {
        sb += str(j)
        j += 1
      } else {
        sb += '-'
      }
      i += 1
    }
    sb.toString()
  }

  private def buildConsensus(seqs: Iterable[String]) = {
    val maxl = seqs.map(s => s.length).max
    val long_seqs = seqs.filter(s => s.length == maxl)
    val consensus = new FractionalString(maxl)
    for (s <- long_seqs) {
      consensus.add(s)
    }
    consensus
  }

  /**
   * Generate all k-subsets of indices
   * from 0,...,n-1
   * @param n
   * Size of superset
   * @param k
   * Size of subsets
   * @return
   * Collection of all possible k-subsets
   */
  def generateAllKSubsets(n: Int, k: Int) = {
    0 until n combinations k
  }
}