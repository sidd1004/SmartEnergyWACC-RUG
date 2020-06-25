package models

import reactivemongo.bson.Macros
import play.api.libs.json.Json

/**
 * Common Implicit Read and Write converters for Reactive Mongo
 */
object JsonFormats {

  implicit val feedFormat = Json.format[EnergyConsumption]
  implicit val jsonRead = Json.format[User]
  implicit val userFormat = Macros.handler[User]
}