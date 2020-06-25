package repositories

import java.net.InetSocketAddress
import java.time.{LocalDate, LocalDateTime, Period, ZoneId, ZoneOffset}
import java.util.Properties

import com.datastax.driver.core.Cluster
import javax.inject.{Inject, Singleton}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.streams.StreamsConfig
import play.api.Configuration
import utility.{Constants, Delimiters}

import scala.collection.JavaConverters._
import utility.Constants.Cassandra._
import utility.Constants.Kafka._
import utility.cassandra.CassandraStartup
import utility.kafka.KafkaUtils
import utility.Constants.Kafka._

@Singleton
final class EnergyConsumptionStreamReader @Inject()(configuration: Configuration, kafkaUtil: KafkaUtils) {

  val keyspace = configuration.get[String](CASSANDRA_KEYSPACE)
  val cluster = CassandraStartup.init(configuration)
  val session = cluster.connect(keyspace)

  val consumer = kafkaUtil.consumer
  consumer.subscribe(List(configuration.get[String](KAFKA_TOPIC)).asJava)

  /**
   * Reads the Kafka stream and returns the messages logged in the topic
   * @return -> Messaged pulled from the topic
   */
  def readKafkaStream(): List[(String, String)] = {
    val records = consumer.poll(3000).asScala
    records.map(rec => {
      (rec.key(), rec.value())
    }).toList
  }

  /**
   * Get the Energy Consumption for a given user by their _id/ProsumerId
   * @param id -> User identification
   * @return   -> Energy Consumption of the User
   */
  def getConsumptionById(id: String): String = {
    // @formatter:off
    session.execute(s"select values from energyCons where prosumerID='$id' order by timestamp desc limit 1").asScala
           .map(x => x.getString(0)).head
    // @formatter:on
  }

  /**
   * Get the Energy Consumption for a given user by their _id/ProsumerId
   * @param id -> User identification
   * @return   -> Energy Consumption of the User
   */
  def getConsumptionHistoryById(id: String, lookbackPeriod: String): String = {
    val endTimestamp = System.currentTimeMillis()
    val starttimestamp = LocalDateTime.now().minusDays(lookbackPeriod.toInt).toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli
    // @formatter:off
    session.execute(s"select values from energyCons where prosumerID='$id' and timestamp > '${starttimestamp}' and timestamp <= '${endTimestamp}'").asScala
           .map(x => x.getString(0)).mkString("^")
    // @formatter:on
  }

}
