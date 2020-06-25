package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import akka.stream.Materializer
import javax.inject.{Inject, Singleton}
import models.EnergyConsumption
import models.JsonFormats._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection
import repositories.EnergyConsumptionStreamReader
import utility.Constants.SmartEnergySystem._
import utility.Constants.User._
import utility.Constants.EnergyConsumption._
import utility.UserTableUtils.getProsumerId

import scala.concurrent.Future

// @formatter:off
@Singleton
class EnergyController @Inject()(val reactiveMongoApi: ReactiveMongoApi,
                                 cc: ControllerComponents,
                                 energyConsumptionStreamReader: EnergyConsumptionStreamReader)
                                (implicit system: ActorSystem, mat: Materializer)
                                extends AbstractController(cc) with MongoController with ReactiveMongoComponents {
// @formatter:on

  /**
   * Fetch the MongoDB Energy Consumption collection
   *
   * @return -> Return the Energy Consumption collection object to be queries upon
   */
  def smartEnergyFuture: Future[JSONCollection] = database.map(_.collection[JSONCollection](ENERGY_CONSUMPTION))


  /**
   * Get the real time energy consumption stream data
   *
   * @return - Energy consumption json string
   */
  @deprecated
  def getEnergyConsumptionStream(): Action[AnyContent] = Action {
    val data = energyConsumptionStreamReader.readKafkaStream()
    Ok(if (data.nonEmpty) {
      data.foreach(x => {
        val ecData = Json.parse(x._2).as[EnergyConsumption]
        smartEnergyFuture.flatMap(_.insert(ordered = false).one(ecData))
      })
      data.head._2
    } else "EMPTY")
  }

  /**
   * Returns a the Energy consumption data for a given user
   *
   * @param userName -> User id
   * @return -> energy consumption json string
   */
  @deprecated
  def energyConsumptionRT(userName: String): Action[AnyContent] = Action {
    Ok(energyConsumptionStreamReader.getConsumptionById(getProsumerId(userName).toString))
  }

  /**
   * Returns a real time stream of Energy consumption data through Websocket and Akka actors
   *
   * @param useName -> User id
   * @return -> Stream of energy consumption json string
   */
  def energyConsumptionWS(useName: String): WebSocket = WebSocket.accept[String, String] {
    request => {
      ActorFlow.actorRef(
        out =>
          EnergyConsumptionWS.props(out, getProsumerId(useName).toString)
      )
    }
  }

  /**
   * Object that supports the Energy consumption Web socket stream, creates a Akka Props object
   */
  object EnergyConsumptionWS {
    def props(out: ActorRef, id: String) = Props(new EnergyConsumptionWS(out, id))
  }

  /**
   * Class that supports the Energy Consumption Web socket stream, holds the logic to retrieve the stream data
   *
   * @param out -> Reference to the output stream object
   * @param id  -> User id for which the data needs to be fetched/streamed
   */
  class EnergyConsumptionWS(out: ActorRef, id: String) extends Actor {
    override def receive: Receive = {
      case msg: String if msg.contains(POISON_PILL_IND) =>
        self ! PoisonPill
      case msg: String =>
        out ! energyConsumptionStreamReader.getConsumptionById(id)
    }
    /**
     * Handles the closing of a websocket connection
     */
  }

  object EnergyConsumptionHistoryWS {
    def props(out: ActorRef, id: String, lookBackPeriod: String = "0") = Props(new EnergyConsumptionHistoryWS(out, id, lookBackPeriod))
  }

  /**
   * Class that supports the Energy Consumption Web socket stream, holds the logic to retrieve the stream data
   *
   * @param out -> Reference to the output stream object
   * @param id  -> User id for which the data needs to be fetched/streamed
   */
  class EnergyConsumptionHistoryWS(out: ActorRef, id: String, lookBackPeriod: String = "0") extends Actor {
    override def receive: Receive = {
      case msg: String if msg.contains(POISON_PILL_IND) =>
        self ! PoisonPill
      case msg: String =>
        out ! energyConsumptionStreamReader.getConsumptionHistoryById(id, lookBackPeriod)
    }
    /**
     * Handles the closing of a websocket connection
     */
  }

  /**
   * Returns a real time stream of Energy consumption data through Websocket and Akka actors
   *
   * @param userName -> User id
   * @return -> Stream of energy consumption json string
   */
  def energyConsumptionHistoryWS(userName: String, lookbackPeriod: String = "0"): WebSocket = WebSocket.accept[String, String] {
    request => {
      ActorFlow.actorRef(
        out =>
          EnergyConsumptionHistoryWS.props(out, getProsumerId(userName).toString, lookbackPeriod)
      )
    }
  }


  /**
   * Find a user entry by their id
   *
   * @param id -> User Id
   * @return -> User record/information
   */
  @deprecated
  def findById(id: String): Action[AnyContent] = Action {

    val query = BSONDocument(PROSUMER_ID -> id)
    Ok(smartEnergyFuture.flatMap(_.find(query).one[BSONDocument]).toString)
  }

}

