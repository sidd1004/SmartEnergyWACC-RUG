package com.wacc.models

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
  val totalConsumption = refrigeration.toLong + plugLoads.toLong + evCharge.toLong + others.toLong
}




