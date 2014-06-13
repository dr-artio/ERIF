package edu.gsu.cs.align.io

import java.io.File
import org.biojava3.core.sequence.io.FastaReaderHelper.readFastaDNASequence
import org.biojava3.core.sequence.io.FastaWriterHelper.writeNucleotideSequence
import org.biojava3.core.sequence.DNASequence
import collection.JavaConversions._
import org.biojava3.core.sequence.compound.{NucleotideCompound, AmbiguityDNACompoundSet}
import org.biojava3.core.sequence.io.{FastaReader, GenericFastaHeaderParser, FileProxyDNASequenceCreator, FastaSequenceParser}

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 7/19/13
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
object FASTAParser {
  def readFASTAFile(path: String): Iterable[DNASequence] = {
    readFASTAFile(new File(path))
  }

  def readFASTAFile(file: File): Iterable[DNASequence] = {
    if (!file.exists || !file.canRead) {
      System.err.println("Reference file doesn't exists or unreadable!")
      System.exit(-1)
    }
    readFastaDNASequence(file).values
  }

  def readReference(path: String): DNASequence = {
    val tmp = readFASTAFileAmbiguity(path)
    tmp.maxBy(_.getLength)
  }

  def writeAsFASTA(seq: String, path: String) = {
    val file = new File(path)
    val dnaSeq = new DNASequence(seq)
    dnaSeq.setOriginalHeader("ExtConsensus")
    writeNucleotideSequence(file, List(dnaSeq))
  }

  def readFASTAFileAmbiguity(file: File): Iterable[DNASequence] = {
    if (!file.exists || !file.canRead) {
      System.err.println("Reference file doesn't exists or unreadable!")
      System.exit(-1)
    }
    val fastaProxyReader = new FastaReader(file,
      new GenericFastaHeaderParser[DNASequence, NucleotideCompound](),
      new FileProxyDNASequenceCreator(file,
        AmbiguityDNACompoundSet.getDNACompoundSet,
        new FastaSequenceParser()
      )
    )
    fastaProxyReader.process().values()
  }

  def readFASTAFileAmbiguity(path: String): Iterable[DNASequence] = {
    readFASTAFileAmbiguity(new File(path))
  }
}
