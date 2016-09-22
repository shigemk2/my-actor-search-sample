package com.example

import akka.actor.{Actor, ActorIdentity, Identify, Props}
import akka.event.Logging

class MyActor extends Actor {
  val log = Logging(context.system, this)

  val identifyId = 1 // 問い合わせ番号
  val child = context.actorOf(Props[MyActor2], name = "myChild")

  var lastSender = context.system.deadLetters

  def receive = {
    case "search" => {
      context.actorSelection("/user/myActor/myChild") ! Identify(identifyId) // 絶対パス

      // その他の指定方法
      // context.actorSelection("../myActor/myChild") ! Identify(identifyId) // 相対パス
      // context.actorSelection("myChild") ! Identify(identifyId) // 相対パス (`./myChild` の意味)
      // context.actorSelection("myChi*") ! Identify(identifyId) // ワイルドカード

      lastSender = sender
    }

    case ActorIdentity(`identifyId`, Some(ref)) => {
      log.info("found")
      lastSender ! ref // 検索結果を返す
    }

    case ActorIdentity(`identifyId`, None) => {
      log.info("not found")
    }

    case s: String => {
      log.info(s)
      child ! s
    }
    case _ => {
    }
  }
}
