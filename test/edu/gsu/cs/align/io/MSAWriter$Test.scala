package edu.gsu.cs.align.io

import org.scalatest.FunSuite
import org.biojava3.alignment.{Alignments, SimpleAlignedSequence}
import org.biojava3.core.sequence.DNASequence
import org.biojava3.core.sequence.compound.NucleotideCompound
import org.biojava3.alignment.template.AlignedSequence.Step
import collection.JavaConversions._
import org.biojava3.alignment.template.Profile

/**
 * Created with IntelliJ IDEA.
 * User: Alex
 * Date: 9/29/13
 * Time: 1:04 AM
 * To change this template use File | Settings | File Templates.
 */
class MSAWriter$Test extends FunSuite {
   test("Create aligned sequence") {
     val wr = new SimpleAlignedSequence[DNASequence, NucleotideCompound](new DNASequence("AA"), List(Step.COMPOUND,Step.GAP,Step.COMPOUND))

     println(wr)
   }
}
