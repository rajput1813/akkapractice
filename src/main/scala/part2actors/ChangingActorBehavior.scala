package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChangingActorBehavior.FussyKid.{KidAccept, KidReject}
import part2actors.ChangingActorBehavior.Mom.{Ask, CHOCOLATE, Food, MomStart, VEGETABLE}

object ChangingActorBehavior extends App{
  object FussyKid{
    case object KidAccept
    case object KidReject
    val HAPPY ="happy"
    val SAD= "sad"
  }
   class FussyKid extends Actor {
     import FussyKid._
     var state ="happy"
     override def receive: Receive = {
       case Food(VEGETABLE) => state=SAD
       case Food(CHOCOLATE) => state =HAPPY
       case Ask(_) =>
         if(state==HAPPY)
           sender() ! KidAccept
         else
           sender() ! KidReject

     }
   }
  object Mom{
    case class MomStart(KidRef: ActorRef)
    case class Food(food:String)
    case class Ask(message:String)
    val VEGETABLE ="veggies"
    val CHOCOLATE= "Chocolate"
  }
  class Mom extends Actor{
    import Mom._
    import FussyKid._
    override def receive: Receive ={
      case MomStart(temp) =>
        temp ! Food(VEGETABLE)
        temp! Ask("do you want to play ? ")
      case KidAccept => println("my kid is happy !")
      case KidReject =>println("my kid is sad ,but he is healthy")

    }
  }

  val system = ActorSystem("ChangingActorBehavior")
  val fussyKid = system.actorOf(Props[FussyKid])
  val mom  = system.actorOf(Props[Mom])
  mom ! MomStart(fussyKid)
}
