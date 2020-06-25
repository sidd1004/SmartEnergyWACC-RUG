package utility

object Constants {
  val EMPTY = ""
  val ESCAPED_QUOTE = "\""

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
    val CASSANDRA_HOTS = "cassandra.hosts"
    val CASSANDRA_PORT = "cassandra.port"
    val CASSANDRA_KEYSPACE = "cassandra.keyspace"
    val CASSANDRA_REPLICATION_FACTOR = "cassandra.replication"
    val CASSANDRA_RECONNECTION_DELAY = 1000
    val CASSANDRA_MAXIMUM_RETRY = 10
    val DURABLE_WRITES_FLAG = "cassandra.durable_writes"
    val CASSANDRA_TABLE_NAME = "cassandra.table_name"
    val CASSANDRA_TABLE_SCHEMA = "cassandra.table.schema"
    val CASSANDRA_KEYSPACE_CLASS_TYPE = "cassandra.keyspace.class"
  }

  object Mongo {
    val USERS_COLLECTION = "Users"
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
    val ENERGY_TO_TRADE = "energyToTrade"
    val PASSWORD = "password"
  }

  object EnergyConsumption {
    val ENERGY_CONSUMPTION = "EnergyConsumption"
  }

  object HTTPCode {
    val NOT_FOUND = "401"
    val OK = "200"
    val SUCCESS = "success"
    val FAILED = "failed"
    val ERROR = "error"

  }

}

object Delimiters {
  val COMMA = ","
  val COLON = ":"
}
