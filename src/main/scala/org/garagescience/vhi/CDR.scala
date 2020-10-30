/*
This is the complementarity determining region class.

We want to be able to convert between floats, longs, & bits.
 */

// we need to keep it simple ...

package org.garagescience.vhi

import scala.collection.immutable.BitSet

object BitSetOps {

  trait BinaryRadix {
    val radix: Int = 2
  }

  // compare the bits in each BitSet, retunr the number of those that match
  def getMatchingBits(b1: BitSet, b2: BitSet): Int = {
    val m = b1 & b2
    m.count(z => true)
  }

  // JKK: hmm ... okay, this is a bit fukd, & i don't know whay
  // JKK: think i'm actually involing this in the wrong way
  // JKK: fucking irritating ...

  implicit class StringToLong(s: String) extends BinaryRadix {

    // maybe have a Try here instead of an Option?
    def toLongFromBinaryString: Option[Long] = {
      try {
        Some(java.lang.Long.parseLong(s, radix))
      } catch {
        case e: NumberFormatException => None
      }
    }
  }

  implicit class StringToInt(s: String) extends BinaryRadix {

    def toIntFromBinaryString: Option[Int] = {
      try {
        Some(java.lang.Integer.parseInt(s, radix))
      } catch {
        case e: NumberFormatException => None
      }
    }
  }

  implicit class StringToShort(s: String) extends BinaryRadix {

    def toShortFromBinaryString: Option[Short] = {
      try {
        Some(java.lang.Short.parseShort(s, radix))
      } catch {
        case e: NumberFormatException => None
      }
    }
  }

  implicit class ShortToBitSet(s: Short) {
    def toBitSet: BitSet = {
      BitSet.fromBitMask(Array(s))
    }
  }

  implicit class IntToBitSet(i: Int) extends BinaryRadix {
    def toBitSet: BitSet = {
      BitSet.fromBitMask(Array(i))
    }
  }

  implicit class LongToBitSet(l: Long) {
    def toBitSet: BitSet = {
      BitSet.fromBitMask(Array(l))
    }
  }

  implicit class DoubleToBitSetFrom(d: Double) {
    def toBitSet: BitSet = {
      BitSet.fromBitMask(Array(java.lang.Double.doubleToRawLongBits(d)))
    }
  }

  implicit class BitSetToBinaryString(bits: BitSet) {
    def toBinaryString: String = {
      val seq = (0 to bits.max) map { x => if (bits.contains(x)) "1" else "0"}
      seq.reverse.mkString
    }
  }

}