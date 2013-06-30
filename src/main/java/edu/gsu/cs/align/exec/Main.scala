package edu.gsu.cs.align.exec

import com.simontuffs.onejar.Boot
import edu.gsu.cs.align.io.SAMParser


/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 6/27/13
 * Time: 12:39 PM
 * To change this template use File | Settings | File Templates.
 */
object Main {
 def main(args: Array[String]) = {
   val sam = SAMParser.readSAMFile("C:\\Users\\aartyomenko\\IdeaProjects\\AlignAndExtendRef\\test.sam.txt")
 }
}
