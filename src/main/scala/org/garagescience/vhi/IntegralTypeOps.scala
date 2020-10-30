package org.garagescience.vhi

import scala.collection.immutable.BitSet
import scala.util.{Failure, Success, Try}
import scala.util.control.Exception
import RangedIntegralOps._

object IntegralTypeOps {

  private val min: Short = 0
  private val max: Short = 255

  trait GetShorty[A] {
    def _toShort(a: A): Short
  }

  implicit val wrapLong: GetShorty[Long] = new GetShorty[Long] {
    def _toShort(a: Long) = a.toShort
  }

  implicit val wrapInt: GetShorty[Int] = new GetShorty[Int] {
    def _toShort(a: Int) = a.toShort
  }

  // can also do this with floaty-point numbers if we want to ...

  def toBitSetOption[A](a: A)(implicit hga: GetShorty[A]): Option[BitSet] = {
    val RF = RangedIntegral(min, max) _
    val s = hga._toShort(a)

    Exception.allCatch.opt(RF(s)) match {
      case Some(ri) => Some(ri.bits)
      case None     => None
    }
  }

  // here's some example code of the sort of thing this now allows
  // e.g., take an Integral & turn it into a BitSet!

  // N.B. we're looking at _toShort now, might change back later ...
  // add1 now works with any type on which value you can call toShort
  /*
  def add1[A](a: A)(implicit hga: GetShorty[A]): Long =
    hga._toShort(a) + 1
   */

  def toBitSet[A] (a: A) (implicit hga: GetShorty[A] ): BitSet = {
    val s: Short = hga._toShort (a)
    val RF = RangedIntegral (min, max) _
    val toBits = RF (s)
    toBits.bits
  }

}