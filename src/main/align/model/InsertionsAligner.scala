package edu.gsu.cs.align.model

import align.model.FractionalString
import scala.collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/23/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
object InsertionsAligner {
  var insertionsTable: Array[List[String]] = null


  /**
   * Align inserted region sequences.
   * Regions come from global alignment.
   * @param seqs
   *             Short inserted fragments
   * @return
   *         Aligned fragments
   */
  def alignInsertedRegion(seqs: List[String]) = {
    val result = new ListBuffer[String]()
    val cons = buildConsensus(seqs)
    val maxl = cons.data.length
    val seqs_to_align = seqs.filter(s => s.length < maxl)
    result ++= seqs.filter(s => s.length == maxl)
    result ++= seqs_to_align.map(s => findBestScoreAndExtend(s, cons))
    result.toList
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
          score *= cons.data(i)(str(j))
          j += 1
        }
        i += 1
      }
      if (score > bestScore) {
        bestScore = score
        best = gaps
      }
    }
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
    sb.toString
  }

  private def buildConsensus(seqs: List[String]) = {
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
   *          Size of superset
   * @param k
   *          Size of subsets
   * @return
   *         Collection of all possible k-subsets
   */
  def generateAllKSubsets(n: Int, k: Int) = {
    0 until n combinations k
  }
}