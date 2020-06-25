package com.wacc.utility.kafka

import java.util.Properties

import com.typesafe.config.ConfigFactory
import com.wacc.models.EnergyConsumption
import com.wacc.utility.Constants.APPLICATION_CONF
import com.wacc.utility.Constants.Cassandra._
import com.wacc.utility.Constants.Kafka._
import com.wacc.utility.cassandra.CassandraStartup
import com.wacc.utility.mongodb.MongoDbUtil
import com.wacc.utility.{Constants, Delimiters}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.streams.StreamsConfig
import play.api.libs.json.Json

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random
import Constants.Mappings._
import Constants._

object KafkaStreamGen extends App {
  val configuration = ConfigFactory.load(APPLICATION_CONF)
  val config: Properties = {
    val prop = new Properties()
    prop.put(StreamsConfig.APPLICATION_ID_CONFIG, configuration.getString(KAFKA_APPLICATION_ID))
    prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getString(KAFKA_URL))
    prop.put(VALUE_DESERIALIZER, configuration.getString(KAFKA_VALUE_DESERIALIZER))
    prop.put(KEY_DESERIALIZER, configuration.getString(KAFKA_KEY_DESERIALIZER))
    prop.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    prop.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    prop
  }

  // read the configuration for Cassandra
  private val cassandraHosts: Seq[String] = configuration.getString(CASSANDRA_HOTS).split(Delimiters.COMMA)
  private val cassandraPort: Int = configuration.getString(CASSANDRA_PORT).toInt
  private val cassandraKeyspace: String = configuration.getString(CASSANDRA_KEYSPACE)
  private val cassandraReplication: Int = configuration.getInt(CASSANDRA_REPLICATION_FACTOR)
  private val cassandraTableName: String = configuration.getString(CASSANDRA_TABLE_NAME)


  val session = CassandraStartup.init(cassandraHosts, cassandraPort, cassandraKeyspace, cassandraTableName, cassandraReplication)

  val producer = new KafkaProducer[String, String](config)

  val prosumerIDs = MongoDbUtil.getListOfUsers(configuration).toList
  var streamData = generateSeed(prosumerIDs, System.currentTimeMillis().toString)

  implicit val feedFormat = Json.format[EnergyConsumption]

  var globalUserSet = ListBuffer[String]()
  var globaUserMap = mutable.HashMap[String, EnergyConsumption]()
  while (true) {
    val timeStamp = System.currentTimeMillis().toString
    val userDump = MongoDbUtil.getListOfUsers(configuration).toList
    val deletedUser = globalUserSet.diff(userDump)
    val newUser = userDump.diff(globalUserSet)

    globalUserSet = globalUserSet.filter(deletedUser.contains)
    globalUserSet.appendAll(newUser)

    // Remove deleted entry
    deletedUser.foreach(key => if (globaUserMap.contains(key)) globaUserMap)

    simulateMinorChanges(globaUserMap, timeStamp)
    newUser.foreach(entry => globaUserMap = globaUserMap ++ mutable.HashMap(entry -> generateRanomdData(entry, timeStamp)))

    val totalUserEnergyConsumption = globaUserMap.map(entry => entry._2.totalConsumption).sum
    val totalEnergyGeneration = {
      val rand = Random.nextDouble()
      if (rand < 0.5) totalUserEnergyConsumption - (totalUserEnergyConsumption * 0.2) else totalUserEnergyConsumption + (totalUserEnergyConsumption * 0.2)
    }
    val totalExcessEnergy = if((totalEnergyGeneration-totalUserEnergyConsumption) <=0) 0 else totalEnergyGeneration-totalUserEnergyConsumption

    // @formatter:off
    val overallEnergyMap = Json.toJson(Map[String, String](Constants.TOTAL_ENERGY_CONSUMPTION -> totalUserEnergyConsumption.toString,
                                                           Constants.TOTAL_ENERGY_GENERATION -> totalEnergyGeneration.toString,
                                                           Constants.TOTAL_EXCESS_ENERGY -> totalExcessEnergy.toString))
    // @formatter:on
    val rec = new ProducerRecord[String, String](configuration.getString(KAFKA_TOPIC), "totalEnergyConsumption", Json.stringify(overallEnergyMap))
    producer.send(rec)
    println(s"PUSHED TO KAFKA ${configuration.getString(KAFKA_TOPIC)}")
    globaUserMap.foreach(entry => {
      session.execute(s"Insert into $cassandraTableName (prosumerID, timestamp, values) values ('" + entry._1 + "','" + timeStamp + "','" + Json.toJson(entry._2).toString() + "')")
    })

    println("INSERTED TO CASSANDRA")
    Thread.sleep(3000)
  }

  def generateSeed(prosumerIds: List[String], timeStamp: String): List[EnergyConsumption] = {
    implicit val feedFormat = Json.format[EnergyConsumption]
    prosumerIds.map(id => generateRanomdData(id, timeStamp))
  }

  def generateRanomdData(prosumerId: String, timeStamp: String): EnergyConsumption = {

    val refrigeration = Random.nextInt(APPLIANCES_THRESHOLD_MAP.getOrElse((REFRIGERATION, LOWER_LIMIT), DEFAULT_LOAD_LIMIT))
    val plugLoads = Random.nextInt(APPLIANCES_THRESHOLD_MAP.getOrElse((PLUGLOADS, LOWER_LIMIT), DEFAULT_LOAD_LIMIT))
    val evCharge = Random.nextInt(APPLIANCES_THRESHOLD_MAP.getOrElse((EVCHARGE, LOWER_LIMIT), DEFAULT_LOAD_LIMIT))
    val others = Random.nextInt(APPLIANCES_THRESHOLD_MAP.getOrElse((OTHERS, LOWER_LIMIT), DEFAULT_LOAD_LIMIT))

    val totalUserEnergyConsumption = refrigeration + plugLoads + evCharge + others
    val totalEnergyGeneration = {
      val rand = Random.nextDouble()
      if (rand < 0.5) totalUserEnergyConsumption - (totalUserEnergyConsumption * 0.2) else totalUserEnergyConsumption + (totalUserEnergyConsumption * 0.2)
    }
    val excessEnergy = if((totalEnergyGeneration-totalUserEnergyConsumption) <=0) 0 else totalEnergyGeneration-totalUserEnergyConsumption
    EnergyConsumption(_id = prosumerId,
      prosumerId = prosumerId,
      timeStamp, refrigeration.toString,
      plugLoads.toString,
      evCharge.toString,
      others.toString,
      totalUserEnergyConsumption.toString,
      totalEnergyGeneration.toString,
      excessEnergy.toString)
  }

  def simulateMinorChanges(userMap: mutable.HashMap[String, EnergyConsumption], timeStamp: String): Unit = {

    def minorFlux(value: EnergyConsumption): EnergyConsumption = {

      def calculateNewValue(str: String, variability: Double, lowerthreshold: Int, upperThreshold: Int): Int = {
        val newValue = str.toInt + (str.toDouble * variability)
        newValue < lowerthreshold || newValue > upperThreshold match {
          case true => lowerthreshold + Random.nextInt((upperThreshold - lowerthreshold) + 1)
          case _ => newValue.toInt
        }
      }

      /* Increment if its 1 else decrement by 10%*/
      val variability = if (Random.nextDouble() >= 0.5) 0.1 else -0.1
      //@formatter:off
      val refrigeration = calculateNewValue(value.refrigeration,
                                            variability,
                                            APPLIANCES_THRESHOLD_MAP.getOrElse((REFRIGERATION, LOWER_LIMIT), DEFAULT_LOAD_LIMIT),
                                            APPLIANCES_THRESHOLD_MAP.getOrElse((REFRIGERATION, UPPER_LIMIT), DEFAULT_LOAD_LIMIT))
      val plugLoads = calculateNewValue(value.plugLoads,
                                        variability,
                                        APPLIANCES_THRESHOLD_MAP.getOrElse((PLUGLOADS, LOWER_LIMIT), DEFAULT_LOAD_LIMIT),
                                        APPLIANCES_THRESHOLD_MAP.getOrElse((PLUGLOADS, UPPER_LIMIT), DEFAULT_LOAD_LIMIT))
      val evCharge = calculateNewValue(value.evCharge,
                                       variability,
                                       APPLIANCES_THRESHOLD_MAP.getOrElse((EVCHARGE, LOWER_LIMIT), DEFAULT_LOAD_LIMIT),
                                       APPLIANCES_THRESHOLD_MAP.getOrElse((EVCHARGE, UPPER_LIMIT), DEFAULT_LOAD_LIMIT))
      val others = calculateNewValue(value.others,
                                     variability,
                                     APPLIANCES_THRESHOLD_MAP.getOrElse((OTHERS, LOWER_LIMIT), DEFAULT_LOAD_LIMIT),
                                     APPLIANCES_THRESHOLD_MAP.getOrElse((OTHERS, UPPER_LIMIT), DEFAULT_LOAD_LIMIT))

      val totalUserEnergyConsumption = refrigeration + plugLoads + evCharge + others
      val totalEnergyGeneration = {
        val rand = Random.nextDouble()
        if (rand < 0.5) totalUserEnergyConsumption - (totalUserEnergyConsumption * 0.2) else totalUserEnergyConsumption + (totalUserEnergyConsumption * 0.2)
      }
      val excessEnergy = if((totalEnergyGeneration-totalUserEnergyConsumption) <=0) 0 else totalEnergyGeneration-totalUserEnergyConsumption

      EnergyConsumption(_id = value._id,
                        prosumerId = value.prosumerId,
                        timeStamp, refrigeration.toString,
                        plugLoads.toString,
                        evCharge.toString, others.toString,
                        totalUserEnergyConsumption.toString,
                        totalEnergyGeneration.toString,
                        excessEnergy.toString)
    }
    // @formatter:on

    globaUserMap.map(entry => {
      entry._1 -> minorFlux(entry._2)
    })

  }
}
