package org.garagescience.vhi

import scala.annotation.tailrec
import scala.collection.immutable.BitSet
import scala.collection.immutable.Vector

object VirusFactory {

  import IntegralTypeOps._

  val randomSequenceLength = 28 * 28
  val maxRandomShort = 255
  val randomShort = scala.util.Random

  protected def generateViralSequence: Vector[Int] =
    (for (i <- 1 to randomSequenceLength) yield randomShort.nextInt(maxRandomShort)).toVector

  // make this a higer-order-function? that way, we could pass in the function
  // to generate the sequence if we wanted to change it ...
  def convertToBitSet: Vector[BitSet] = {
    val xs: Vector[Int] = generateViralSequence
    (xs.flatMap(x => toBitSetOption(x))).toVector
  }

  def createViralStrainWithSeq: SingleStrandedWithSeq = {
    val genes: Vector[BitSet] = convertToBitSet
    val sequence: Vector[Int] = generateViralSequence
    SingleStrandedWithSeq(genes, sequence)
  }

  def createViralStrain: SingleStranded = {
    val genes: Vector[BitSet] = convertToBitSet
    SingleStranded(genes)
  }

}

sealed trait Virus[U] {

  def genes: Vector[U]

  def length: Int
}

sealed trait AdaptiveVirus extends Virus[BitSet] {

  // protected? private[vhi]?

  def getRandomGene = scala.util.Random.nextInt(genes.length)

  //def getRandomSite(bits: BitSet) = scala.util.Random.nextInt(bits.toList.length)
  def getRandomSite(bits: BitSet) = scala.util.Random.nextInt(length)

  // this is a pre-selected BitSet
  def mutate(bits: BitSet): BitSet = {
    val mutant = BitSet(getRandomSite(bits))
    bits ^ mutant
  }

  def mutateHotspots(originalGenes: Vector[BitSet] = genes,
                     numberOfHotspots: Int = 10): Vector[BitSet] = {
    @tailrec
    def _mutateHotspots(genesToMutate: List[Int],
                        genes: Array[BitSet]): Array[BitSet] = {
      genesToMutate match {
        case Nil => genes
        case x :: xs =>
          genes(x) = mutate(genes(x))
          _mutateHotspots(xs, genes)
      }
    }

    // make a copy of the genes that we're going to mutate
    val genes = originalGenes.toArray
    val randomGenes: List[Int] = {
      for (i <- 1 to numberOfHotspots) yield getRandomGene
    }.toList
    val mutatedGenes = _mutateHotspots(randomGenes, genes)
    mutatedGenes.toVector
  }

}

final case class SingleStrandedWithSeq(val genes: Vector[BitSet],
                                       val sequence: Vector[Int],
                                       val length: Int = 8) extends AdaptiveVirus

final case class SingleStranded(val genes: Vector[BitSet],
                                val length: Int = 8) extends AdaptiveVirus


