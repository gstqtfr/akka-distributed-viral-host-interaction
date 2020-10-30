package org.garagescience.vhi

case class Work(workId: String, job: IndexedSeq[Int]) extends CborSerializable

// JKK: hmm ... result is of type Any (?!) ...
case class WorkResult(workId: String, result: Any) extends CborSerializable
