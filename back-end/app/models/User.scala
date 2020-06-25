package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.api.MongoDriver

import reactivemongo.bson.Macros

/**
 * Provides schema a User record
 * Also handles implicit conversion required by Reactive Mongo
 *
 * @param _id        -> User ID to be written to MongoDb as _id
 * @param prosumerId -> Prosumer Id
 * @param userName   -> Name of the User
 * @param password   -> Credentials of the User
 */
case class User(_id: String, prosumerId: String, userName: String, password: String, energyToTrade: String = "0") extends MongoDriver {

  implicit val format: OFormat[User] = Json.format[User]
  implicit val energyConsFormat = Macros.handler[EnergyConsumption]
  implicit val writer = Macros.writer[User]
  implicit val reader = Macros.reader[User]
  implicit val jsonRead = Json.format[User]
}
