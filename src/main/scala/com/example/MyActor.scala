package com.example

import akka.actor.{Actor, ActorIdentity, Identify, Props}
import akka.event.Logging

class MyActor extends Actor {
  val log = Logging(context.system, this)

  val identifyId = 1
  val child = context.actorOf(Props[MyActor2], name = "myChild")

  var lastSender = context.system.deadLetters

  def receive = {
    case "search" => {
      context.actorSelection("/user/myActor/myChild") ! Identify(identifyId)

      lastSender = sender
    }

    case ActorIdentity(`identifyId`, Some(ref)) => {
      log.info("found")
      lastSender ! ref
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
