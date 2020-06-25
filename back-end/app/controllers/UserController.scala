package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject.{Inject, Singleton}
import models.User
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, Macros}
import reactivemongo.play.json._
import utility.Constants.HTTPCode
import utility.Constants.Mongo._
import utility.{Constants, UserTableUtils}
import utility.UserTableUtils.genUniqueProsumerID

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.JsonFormats._


// @formatter:off
@Singleton
class UserController @Inject()(val reactiveMongoApi: ReactiveMongoApi,
                               cc: ControllerComponents)
                              (implicit system: ActorSystem, materializer: Materializer)
                              extends AbstractController(cc) with MongoController with ReactiveMongoComponents {
// @formatter:on

  /* Akka materializer object */
  implicit def mat: Materializer = materializer

  /* Connect to the User collection present in MongoDb */
  def usersCollection: Future[BSONCollection] = database map (_.collection(USERS_COLLECTION))

  implicit val userFormat = Macros.handler[User]

  /**
   * Handler to deal with User authentication; returns 200 if the user is valid
   *
   * @param userName -> Name of the user who is requesting authentication
   * @param password -> User's credentials
   * @return -> Status of the authentication process
   */
  def authenticateLogin(userName: String, password: String): Action[AnyContent] = Action.async {
    UserTableUtils.getUser(userName, usersCollection).map(user => {
      user.map(u => {
        if (userName == u.userName && password == u.password) Ok(HTTPCode.OK) else NotFound(HTTPCode.NOT_FOUND)
      }).getOrElse(NotFound)
    })
  }

  /**
   * Handler to create a new user
   *
   * @param userName -> New user's name
   * @param password -> New user's credentials
   * @return -> Status of the user creation process
   */
  def createNewUser(userName: String, password: String): Action[AnyContent] = Action.async(
    // @formatter:off
    UserTableUtils.getUserByUserName(userName, usersCollection).flatMap(user => {
      if (user.isEmpty)
        UserTableUtils.addNewUser(userName, password, usersCollection)
                      .map(_ => Created(s"UserName: $userName successfully Created"))
      else Future.successful(BadRequest(s"UserName $userName already in use"))
    })
    // @formatter:on
  )

  /**
   * Handles deletion of a user from the database
   *
   * @param userName -> User name to be delete
   * @return -> Status of the deletion operation
   */
  def deleteUser(userName: String): Action[AnyContent] = Action.async {
    UserTableUtils.removeUser(userName, usersCollection).map {
      case Some(user) => Ok(s"User: ${user.userName} Deleted")
      case _ => NotFound(s"Failed to Delete the User: ${userName}")
    }
  }

  /**
   * Handles and changes that is required to be made to the existing users
   *
   * @param userName    -> User to be modified
   * @param password    -> User's credentials
   * @param newPassword -> New user password
   * @return
   */
  def changePassword(userName: String,
                     password: String,
                     newPassword: String = Constants.EMPTY): Action[AnyContent] = Action.async {
    // @formatter:off
    UserTableUtils.updateUser(userName, password, newPassword, usersCollection).map {
      case Some(user) => Forbidden("User name cannot be Changed!")
      case _ => NotFound(s"Failed to Update the user: ${userName}")
    }
    // @formatter:on
  }

  /**
   * Sets the user requested energy to be traded in mongoDb
   *
   * @param userName      -> User to be modified
   * @param energyToTrade -> New user password
   * @return -> Status of operation
   */
  def setEnergyToTrade(userName: String, password: String, energyToTrade: String) = Action.async {
    val id = genUniqueProsumerID(userName)
    UserTableUtils.updateTradeValue(userName, password, energyToTrade, usersCollection).map {
      case Some(user) => Ok("Energy trade value set")
      case _ => NotFound("User not found")
    }
  }


  /**
   * Gets the user information from the mongoDb
   *
   * @param userName -> User for whom the information has to be retrieved
   * @return -> user information in json format
   */
  def getUserInformation(userName: String): Action[AnyContent] = Action.async {
    UserTableUtils.getUser(userName, usersCollection).map {
      case Some(user) => Ok(Json.toJson(user))
      case _ => NotFound(s"Failed to retrieve the user information for : ${userName}")
    }
  }

}

