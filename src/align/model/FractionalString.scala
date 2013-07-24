package align.model

import scala.collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 7/23/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
class FractionalString(len: Int) {
  private val nucls = List[Char]('A','C','T','G')

  val data = {
    (1 to len).map(i => mutable.Map(nucls.map(s => (s, 0.1)).toSeq: _*))
  }

  def this(str: String) = {
    this(str.length)
    var i = 0
    for (d <- data) {
      val cur_symb = str(i)
      if (d.contains(cur_symb))
        d(cur_symb) = 1.0
      else {
        val avg = 1.0 / d.size
        d.keys.foreach(k => d(k) = avg)
      }
      i += 1
    }
  }

  def add(str: String) = {
    var i = 0
    for (d <- data) {
      d(str(i)) += 1
      i += 1
    }
  }
}
