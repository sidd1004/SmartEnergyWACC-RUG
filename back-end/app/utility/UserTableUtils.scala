package utility

import models.JsonFormats._
import models.User
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONDocument, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utility.Constants.User._

/**
 * Repository of User to perform Transaction against MongoDB
 */
object UserTableUtils {

  /**
   * Fetch the user from the MongoDB
   *
   * @param userName        -> User Name for which Information has to be fetched
   * @param usersCollection -> MongoDB user collection
   * @return -> User information
   */
  def getUser(userName: String, usersCollection: Future[BSONCollection]): Future[Option[User]] = {
    usersCollection.flatMap(x =>
      x.find(BSONDocument(_ID -> genUniqueProsumerID(userName))).one[User]
    )
  }

  /**
   * Fetch the user from the MongoDB
   *
   * @param userName        -> User Name for which Information has to be fetched
   * @param usersCollection -> MongoDB user collection
   * @return -> User information
   */
  def getUserByUserName(userName: String, usersCollection: Future[BSONCollection]): Future[Option[User]] = {
    usersCollection.flatMap(table => {
      table.find(BSONDocument(USER_NAME -> userName)).one[User]
    })
  }

  /**
   * Add a new user entry to the MongoDB
   *
   * @param userName       -> User Name of the new user
   * @param password       -> Login credentials of the new user
   * @param userCollection -> MongoDB users collection object
   * @return -> Status of the operation
   */
  def addNewUser(userName: String, password: String, userCollection: Future[BSONCollection]): Future[WriteResult] = {
    val prosumerId = genUniqueProsumerID(userName)
    val user = User(prosumerId, prosumerId, userName, password)
    userCollection.flatMap(_.insert(ordered = false).one(user))
  }

  /**
   * Remove a user entry from the MongoDb
   *
   * @param userName       -> User name to be removed
   * @param userCollection -> Users collection of the MongoDb
   * @return -> Status of the operation
   */
  def removeUser(userName: String, userCollection: Future[BSONCollection]): Future[Option[User]] = {
    val prosumerId = genUniqueProsumerID(userName)
    userCollection.flatMap(_.findAndRemove[BSONDocument](BSONDocument(_ID -> prosumerId)).map(_.result[User]))
  }

  /**
   * Update a users information in the MongoDb
   *
   * @param userName       -> User name
   * @param password       -> Current credential of the user
   * @param newPassword    -> New credentials of the user
   * @param userCollection -> Users collection of the MongoDB
   * @return -> Status of the update operation
   */
  def updateUser(userName: String, password: String, newPassword: String = "",
                 userCollection: Future[BSONCollection]): Future[Option[User]] = {
    val prosumerId = genUniqueProsumerID(userName)
    val user = User(prosumerId, prosumerId, userName, if (newPassword.nonEmpty) newPassword else password)
    userCollection.flatMap(_.findAndUpdate(BSONDocument(_ID -> prosumerId), user).map(_.result[User]))
  }

  def updateTradeValue(userName: String, password: String, energyToTrade: String = "",
                       userCollection: Future[BSONCollection]): Future[Option[User]] = {
    val prosumerId = genUniqueProsumerID(userName)
    val user = User(prosumerId, prosumerId, userName, password, energyToTrade)
    userCollection.flatMap(_.findAndUpdate(BSONDocument(_ID -> prosumerId), user).map(_.result[User]))
  }

  /**
   * Generate a unique Id for a user based on their UserName
   */
  val genUniqueProsumerID: String => String = (userName: String) => {
    Math.abs(userName.hashCode).toString
  }

  val getProsumerId: String => String = (userName: String) => {
    genUniqueProsumerID(userName)
  }
}
