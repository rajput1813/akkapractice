package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App {
  val actorSystem = ActorSystem("rahul")
  print(actorSystem.name)

  class WordCountActor extends Actor {
    var totalWords = 0;

    def receive: PartialFunction[Any, Unit] = {
      case message: String =>
        println(s"[word counter] i have recieved : $message")
        totalWords += message.split(" ").length
      case msg => println(s"[word counter] can  not understand: ${msg.toString}")

    }


  }

  val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter")
  val anotherwordCounter = actorSystem.actorOf(Props[WordCountActor], "anotherwordCounter")

  wordCounter ! "I am learning Akka and it's pretty damn cool "
  anotherwordCounter ! "a different actor"



  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case "h" => println(s"Hii my name is $name")
      case _ => println("not found")
    }
  }

  val person = actorSystem.actorOf(Props(new Person("Rahul")))
  person ! "h"


}
