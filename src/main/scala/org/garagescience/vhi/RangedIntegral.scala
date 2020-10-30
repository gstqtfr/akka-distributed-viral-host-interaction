package org.garagescience.vhi

import scala.collection.immutable.BitSet
import BitSetOps._

object RangedIntegralOps {

  abstract class AbstractRangedIntegral(lb: Short, ub: Short) {
    require(lb <= value && value <= ub,
      s"Requires $lb <= $value <= $ub to hold")

    // this will require us to transform "value" into a Short
    def value: Short
  }

  // this will, of course, work with any Integral type as is ...
  case class RangedIntegral(val lb: Short,
                            val ub: Short)(val value: Short) extends AbstractRangedIntegral(lb, ub) {
    val bits: BitSet = value.toBitSet
  }

  // usage: we *partially construct* the object, then apply it like this:

  // val RF = RangedIntegral(0, 255) _
  // RF(1L.toShort)
  // RF(-1L.toShort) // Exception
  // RF(255L.toShort)
  // RF(256L.toShort) // Exception

  // & we need to define the other classes thatwe might bump into

  // err. hang on, we don;t need extra classes - we just need the one class ...

}