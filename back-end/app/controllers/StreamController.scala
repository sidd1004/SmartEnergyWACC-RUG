package controllers

import java.util.Properties

import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import akka.stream.scaladsl.{Flow, RestartSource, Sink, Source}
import akka.stream.{ActorAttributes, Materializer, Supervision}
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import utility.kafka.KafkaUtils

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import utility.Constants.Kafka._
import utility.Constants.SmartEnergySystem._

import scala.util.{Failure, Success, Try}

@Singleton
class StreamController @Inject()(implicit ec: ExecutionContext,
                                 cc: ControllerComponents,
                                 config: Configuration,
                                 kafka: KafkaUtils, mat: Materializer) extends AbstractController(cc) {

  /**
   * A Websocket backed by a Generic Kafka Streamer
   * @return -> The message present in the TOPIC_NAME
   */
  @deprecated
  def akkaStreamSocket: WebSocket = WebSocket.acceptOrResult[String, String] {
    request =>
      request.queryString.get(TOPIC_NAME).map(_.toString) match {
        case None => Future(Left(NotFound))
        case Some(topic) => Future(Right(eventsFlow(topic)))
        case _ => Future(Left(Forbidden))
      }
  }

  /**
   * Private method to help with supervision during akka flow
   */
  private val parseEvents: Flow[String, String, NotUsed] = Flow[String]
    .map(x => x)
    .withAttributes(ActorAttributes.supervisionStrategy({ ex =>
      Supervision.Resume
    }))

  /**
   * Reads the message from the kafka topic and returns the message
   * @param topic -> Kafka Topic to be read
   * @return      -> Kafka message within the topic
   */
  private def eventsFlow(topic: String): Flow[Any, String, NotUsed] = {

    //@formatter:off
    /* connect to Kafka to get live streaming */
    val liveEvents: Source[String, _] = RestartSource.withBackoff(1.seconds, 2.seconds, 0.2) { () => kafka.source(topic) }
                                                     .map(_.value)
                                                     .via(parseEvents)
    // @formatter:on
    Flow.fromSinkAndSource(Sink.ignore, liveEvents)
  }

  /* Declaring a implicit Actor system to support the akka kafka code snippet */
  implicit val system = ActorSystem()

  /**
   * A Websocket backed by a Generic Kafka Streamer and Akka Actor system
   * @return -> Message read from the kafka topic
   */
  def akkaActorSocket: WebSocket = WebSocket.acceptOrResult[String, String] { request =>
    Future.successful({
      /* Get the kafka topic to be read*/
      val topic = request.queryString.get(TOPIC_NAME)
      topic match {
        case Some(topic) =>
          Right(ActorFlow.actorRef(
            out =>
              KafkaActor.props(out, topic.head)
          ))
        case _ => Left(Forbidden)
      }})
  }

  /**
   * Supports the Kafka Actor read by creating the Props object
   */
  object KafkaActor {
    def props(out: ActorRef, id: String) = Props(new KafkaActor(out, id))
  }

  val consumerObj = kafka.consumer
  /* Classed used the Kafka Actor Akka Actor to create the Props object
  *  Handles the logic to read the kafka message and return the message back to the websocket
  *  */
  class KafkaActor(out: ActorRef, topic: String) extends Actor {
    override def receive: Receive = {
      case msg: String if msg.contains(POISON_PILL_IND) =>
        /* Used to kill the Actor when it is encountered with a posion_pill kill indicator */
        self ! PoisonPill
      case msg: String =>
        out ! {
          /* Connect to the kafka topic and read the messages from the topic */
          consumerObj.subscribe(List(config.get[String](KAFKA_TOPIC)).asJava)
          val stream = kafka.getStream[String](consumerObj)
            Try(stream.head._2) match {
              case Success(value) => value
              case Failure(exception) => {
                println("EXCEPTION CAUGHT")
                EMPTY_CONSUMER_MESSAGE
              }
            }
        }
    }
  }

}
