package com.example

import akka.actor.{ActorIdentity, ActorRef, ActorSystem, Identify, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object Hello {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("mySystem")
    implicit val timeout = Timeout(5 seconds)

    // アクターの生成
    val props = Props[MyActor]
    val actor = system.actorOf(props, name = "myActor")
    Thread.sleep(1000)

    // アクター内で別のアクターの検索を要請
    val future: Future[Any] = actor ? "search"
    val result = Await.result(future, timeout.duration).asInstanceOf[ActorRef]
    result ! "hi child"

    // アクター外で検索する場合
    val identifyId = 1
    val future2: Future[Any] = system.actorSelection("/user/myActor") ? Identify(identifyId)
    val result2 = Await.result(future2, timeout.duration)
    result2 match {
      case ActorIdentity(`identifyId`, Some(ref)) => {
        ref ! "hi"
      }
      case ActorIdentity(`identifyId`, None) => {
        println("not found")
      }
      case _ => {
      }
    }

    actor ! "out"

    Thread.sleep(1000)
    // val future3: Future[Any] = system.actorSelection("/user/badActor") ? Identify(identifyId)
    // val result3 = Await.result(future3, timeout.duration)
    // result3 ? "not found"

    system.terminate()
  }
}
