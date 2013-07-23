package edu.gsu.cs.align.model

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/23/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
object InsertionsAligner {
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
    if (k > n) { }
    0 until n combinations k
  }
}
