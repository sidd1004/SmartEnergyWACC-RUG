package models

import play.api.libs.json.{Json, OFormat}
import reactivemongo.bson.Macros

/* Case class to provide schema for Energy Consumption object
* Contains implicit handlers
* */
case class EnergyConsumption(_id: String,
                             prosumerId: String,
                             timestamp: String,
                             refrigeration: String,
                             plugLoads: String,
                             evCharge: String,
                             others: String = "0",
                             totalEnergyConsumption: String = "0",
                             totalEnergyGeneration: String = "0",
                             excessEnergy: String = "0") {

  /* Implicit writer[T] to handle the data type conversion during query operations */
  implicit val format: OFormat[EnergyConsumption] = Json.format[EnergyConsumption]
  implicit val userFormat = Macros.handler[EnergyConsumption]

  val totalConsumption = refrigeration.toLong + plugLoads.toLong + evCharge.toLong + others.toLong
}




