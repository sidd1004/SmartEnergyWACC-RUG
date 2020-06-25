package com.wacc.utility

object Constants {
  val EMPTY = ""
  val ESCAPED_QUOTE = "\""
  val APPLICATION_CONF = "application.conf"
  val TOTAL_ENERGY_CONSUMPTION = "totalEnergyConsumption"
  val TOTAL_ENERGY_GENERATION = "totalEnergyGeneration"
  val TOTAL_EXCESS_ENERGY = "totalExcessEnergy"

  object Kafka {
    /* KAFAK Utils Constants */
    val ACTOR_SYSTEM = "kafka"
    val KAFKA_URL = "kafka.url"
    val AUTO_OFFSET_RESET_CONFIG = "earliest"
    val KAFKA_TOPIC = "kafka.topic"
    val EMPTY_CONSUMER_MESSAGE = "Consumer Topic is current empty"
    val PROP_GROUP_ID = "group.id"
    val KAFKA_GROUP_ID = "kafka.group.id"
    val VALUE_DESERIALIZER = "value.deserializer"
    val KEY_DESERIALIZER = "key.deserializer"
    val KAFKA_VALUE_DESERIALIZER = "kafka.value.deserializer"
    val KAFKA_KEY_DESERIALIZER = "kafka.key.deserializer"
    val KAFKA_APPLICATION_ID = "kafka.application.id"
    val KAFKA_CONSUMER_POLL_TIMEOUT = "kafka.consumer.timeout"
  }

  object Cassandra {
    /* Cassandra Utils Constants */
    val CASSANDRA_HOTS = "cassandra.datagen.hosts"
    val CASSANDRA_PORT = "cassandra.datagen.port"
    val CASSANDRA_KEYSPACE = "cassandra.datagen.keyspace"
    val CASSANDRA_REPLICATION_FACTOR = "cassandra.datagen.replication"
    val CASSANDRA_RECONNECTION_DELAY = 1000
    val CASSANDRA_MAXIMUM_RETRY = 10
    val DURABLE_WRITES_FLAG = "cassandra.datagen.durable_writes"
    val CASSANDRA_TABLE_NAME = "cassandra.datagen.table_name"
    val CASSANDRA_TABLE_SCHEMA = "cassandra.datagen.table.schema"
    val CASSANDRA_KEYSPACE_CLASS_TYPE = "cassandra.datagen.keyspace.class"
  }

  object Mongo {
    val USERS_COLLECTION = "Users"
    val MONGO_URI = "mongodb.uri"
    val MONGO_DB = "mongodb.db"
  }

  object SmartEnergySystem {
    val POISON_PILL_IND = "close"
    val WEBSOCKET_TERMINATION_MSG = "Closing the websocket connection."
    val TOPIC_NAME = "topic"
  }

  object User {
    val PROSUMER_ID = "prosumerId"
    val USER_NAME = "userName"
    val _ID = "_id"
  }

  object EnergyConsumption {
    val ENERGY_CONSUMPTION = "EnergyConsumption"
  }

  object HTTPCode {
    val NOT_FOUND = "401"
    val OK = "200"

  }

  val REFRIGERATION = "refrigeration"
  val PLUGLOADS = "plugloads"
  val EVCHARGE = "evcharge"
  val OTHERS = "others"
  val LOWER_LIMIT = "lowerLimit"
  val UPPER_LIMIT = "upperLimit"
  val DEFAULT_LOAD_LIMIT = 100

  object Mappings {
    val APPLIANCES_THRESHOLD_MAP = Map(
      (REFRIGERATION, LOWER_LIMIT) -> 60,
      (REFRIGERATION, UPPER_LIMIT) -> 120,
      (PLUGLOADS, LOWER_LIMIT) -> 100,
      (PLUGLOADS, UPPER_LIMIT) -> 150,
      (EVCHARGE, LOWER_LIMIT) -> 150,
      (EVCHARGE, UPPER_LIMIT) -> 200,
      (OTHERS, LOWER_LIMIT) -> 200,
      (OTHERS, UPPER_LIMIT) -> 250,
    )
  }

}

object Delimiters {
  val COMMA = ","
  val COLON = ":"
}
