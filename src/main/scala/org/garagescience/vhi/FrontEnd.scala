package org.garagescience.vhi

import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

import akka.actor.typed._
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import akka.util.Timeout
import org.garagescience.vhi.WorkManager.SubmitWork

import scala.concurrent.duration._
import scala.util.Failure
import scala.util.Success

/**
 * Dummy front-end that periodically sends a workload to the work manager.
 */
object FrontEnd {

  sealed trait Command
  case object Tick extends Command
  case object Failed extends Command
  case object Retry extends Command
  case object WorkAccepted extends Command

  private def nextWorkId(): String = UUID.randomUUID().toString

  def apply(workManager: ActorRef[SubmitWork]): Behavior[Command] = Behaviors.setup { ctx =>
    Behaviors.setup { ctx =>
      Behaviors.withTimers { timers =>
        new FrontEnd(workManager, ctx, timers).idle(0)
      }
    }
  }

}

class FrontEnd private (
    workManager: ActorRef[SubmitWork],
    ctx: ActorContext[FrontEnd.Command],
    timers: TimerScheduler[FrontEnd.Command]) {
  import FrontEnd._

  private val maxInt = 255
  private val antigenPopSize = 500
  private val antigenSize = 50
  private val r = scala.util.Random
  private def createAntigen = for (i <- 1 to antigenSize) yield r.nextInt(maxInt)
  private val antigenPool = for (i <- 1 to antigenPopSize) yield createAntigen

  private def getWork() = {
    antigenPool(r.nextInt(antigenPopSize))
  }

  def idle(workCounter: Int): Behavior[Command] = {
    val nextTick = ThreadLocalRandom.current.nextInt(3, 10).seconds
    timers.startSingleTimer("tick", Tick, nextTick)
    Behaviors.receiveMessage {
      case Tick =>
        // JKK: change this to getWork func above ...
        busy(workCounter + 1, Work(nextWorkId(), getWork()))
      case _ =>
        Behaviors.unhandled
    }
  }

  def busy(workCounter: Int, workInProgress: Work): Behavior[Command] = {
    def sendWork(work: Work): Unit = {
      implicit val timeout: Timeout = Timeout(5.seconds)
      ctx.ask[SubmitWork, WorkManager.Ack](workManager, replyTo => SubmitWork(work, replyTo)) {
        case Success(_) => WorkAccepted
        case Failure(_) => Failed
      }
    }

    sendWork(workInProgress)

    Behaviors.receiveMessage {
      case Failed =>
        ctx.log.info("Work {} not accepted, retry after a while", workInProgress.workId)
        timers.startSingleTimer("retry", Retry, 3.seconds)
        Behaviors.same
      case WorkAccepted =>
        ctx.log.info("Got ack for workId {}", workInProgress.workId)
        idle(workCounter)
      case Retry =>
        ctx.log.info("Retrying work {}", workInProgress.workId)
        sendWork(workInProgress)
        Behaviors.same
      case Tick =>
        Behaviors.unhandled
    }
  }

}
