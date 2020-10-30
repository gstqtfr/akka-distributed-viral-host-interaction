package org.garagescience.vhi

import java.util.concurrent.ThreadLocalRandom

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import org.garagescience.vhi.Worker.WorkComplete

import scala.concurrent.duration._

/**
  * Work executor is the actor actually performing the work.
  */
object WorkExecutor {




  /**
    * TODO:
    *
    * a) add a val pointing to a mutable Map of [affinity, genesequence]
    * b) for every ExecuteWork, create an actor with a genesequence
    * c) accept a message containing the affinity from the worker, update if it's > the
    *    current affinity
    *  d) er, that's it ...
    */

  case class ExecuteWork(n: IndexedSeq[Int], replyTo: ActorRef[WorkComplete])

  def apply(): Behavior[ExecuteWork] = {
    Behaviors.setup { ctx =>
      Behaviors.receiveMessage { doWork =>
        ctx.log.info("Doing work {}", doWork)

        /*
        TODO: here's where we Do Stuff
        TODO: a) we need to have some genes here which we can mutate
        TODO: b) er, we need to clone & mutate the buggers
        TODO: c) we need to produce an affinity score
         */


        val n = doWork.n

        /*
        val n2 = n * n
        val result = s"$n * $n = $n2"
        */

        val result = s"$n"

        // simulate that the processing time varies
        val randomProcessingTime = ThreadLocalRandom.current.nextInt(1, 3).seconds

        ctx.scheduleOnce(
          randomProcessingTime,
          doWork.replyTo,
          WorkComplete(result)
        )

        Behaviors.same
      }
    }
  }
}
