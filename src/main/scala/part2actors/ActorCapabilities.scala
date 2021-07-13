package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ActorCapabilities.BankAccount.{Deposit, Statement, TransactionFailure, TransactionSuccess, Withdraw}
import part2actors.ActorCapabilities.Counter.{Decrement, Increment, Print}
//import part2actors.ActorCapabilities.Person.LiveTheLife
// actor basic
object ActorCapabilities extends App {
  class SimpleActor extends Actor {
   // context.self
    override def receive: Receive = {
      case "hii" => context.sender()! "hello there"
      case message :String => println(s"[${self}] i have received : $message")
      case number :Int => println(s"[simple actor] i have received : $number")
      case SpecialMessage(contents) => println(s"[simple actor] I have received something Special : $contents")
      case SendMessageToYourself(content) =>
        self ! content
      case SayHiTo(ref) =>ref ! "hii"
      case WirelessPhoneMessage(content,ref) => ref forward (content +"s") //i keep the original sender of the wpm
    }

  }
  val System = ActorSystem("actorCapabilitiesDemo")
  val SimpleActor = System .actorOf(Props[SimpleActor],"simpleActor")

  SimpleActor ! "hello, actor"
  //1- measeg can be of any type
  //(a) message must be immutable
  //(b) meaasge must be seriazable
  //(c) in practice use case classes and case object
  SimpleActor ! 42
case class SpecialMessage(contents: String)
  SimpleActor ! SpecialMessage  (" some special content")
  //2 actors have information about their context and about themselves
  //context.self ===  'this' keyword in oop
case class  SendMessageToYourself(content:String)
  SimpleActor ! SendMessageToYourself("i am and actor and i am proud of it")
val alice = System.actorOf(Props[SimpleActor],"alice")
  val bob = System.actorOf(Props[SimpleActor],"bob")
  case class SayHiTo(ref:ActorRef)
  alice ! SayHiTo(bob)
  //4 - dead letters
 // alice ! "hii"
  case class WirelessPhoneMessage(content: String,ref:ActorRef)
  alice ! WirelessPhoneMessage(" hi",bob)

  /**
   * Extercises
   * 1.a counter actor
   * -increment
   * -decrement
   * -print
   *
   * 2. a bank account as an actor
   * receives
   * - Deposit an amount
   * - withdraw an amount
   * - Statement replies with
   * - success
   * failure
   *
   */
  //Domain of the counter
  object Counter{
    case object Increment
    case object Decrement
    case object Print
  }
  class Counter extends Actor{
    import Counter._
    var count=0;
    override def receive: Receive = {
      case Increment => count+=1
      case Decrement =>count-=1
      case Print => println(s"[counter] My current Count is $count")


    }
  }
  import Counter._
  val counter  =System.actorOf(Props[Counter],"MyCounter")
  (1 to 5).foreach(- => counter ! Decrement)
  (1 to 9).foreach (_ => counter ! Increment )
  counter !  Print

  object BankAccount {

    case class  Deposit (amount :Int)
    case class Withdraw(amount: Int)
    case object Statement
    case class TransactionSuccess(message:String)
    case class TransactionFailure(message:String)
  }
  class BankAccount extends Actor{
    import BankAccount._
     var funds =0
    override def receive: Receive = {
      case Deposit(amount)=>
        if(amount<0)sender() ! TransactionFailure("invali deposit amount")
        else {
          funds+=amount
          sender() ! TransactionSuccess(s"Successfully Deposited Rs $amount")
        }
      case Withdraw(amount)=>
        if(amount<0)sender() ! TransactionFailure("invali withdraw amount")
        else if(amount >funds)
          sender() ! TransactionFailure("insufficient funds")
        else {
          funds-=amount
          sender() ! TransactionSuccess(s" Successfully withdrew  amount =Rs $amount")
        }
      case Statement => sender() ! s"your balance is $funds"

    }
  }
//  import BankAccount ._
val account =System.actorOf(Props[BankAccount],"bankAccount")
  account ! Deposit(5000)
  account ! Withdraw(1000)
  account ! Statement

//object Person  {
//  case class LiveTheLife(account:ActorRef)
//}
//  class Person extends Actor{
//    import Person._
//
//    override def receive: Receive ={
//      case LiveTheLife(account)=>
//        account! Deposit(5000)
//        account ! Withdraw(1500)
//        account ! Withdraw(500)
//        account ! Statement
//      case message =>println((message.toString))
//    }
//  }
//  val account =System.actorOf(Props[BankAccount],"bankAccount")
//  val person= System.actorOf(Props[Person],"billionaire")
//  person ! LiveTheLife(account)

}
