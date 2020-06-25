package utility.kafka

import java.util.{Properties, UUID}

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerMessage, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.{Done, NotUsed}
import javax.inject.{Inject, Singleton}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord, KafkaConsumer}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import org.apache.kafka.streams.StreamsConfig
import play.api.Configuration
import utility.Constants.Kafka._
import scala.collection.JavaConverters._


import scala.concurrent.Future

//Ref: https://www.programcreek.com/scala/akka.kafka.ConsumerSettings
@Singleton
final class KafkaUtils @Inject()(configuration: Configuration) {
  private val actorSystem: ActorSystem = ActorSystem(ACTOR_SYSTEM)
  private val kafkaUrl: String = configuration.get[String](KAFKA_URL)

  /**
   * Sets the Kafka producers settings
   * @return -> Producer settings object
   */
  private def producerSettings: ProducerSettings[Array[Byte], String] = {
    ProducerSettings(actorSystem, new ByteArraySerializer, new StringSerializer).withBootstrapServers(kafkaUrl)
  }

  /**
   * Sets the Kafka consumers settings
   * @param group -> Group id of the kafka consumer
   * @return      -> Kafka consumer settings object
   */
  def consumerSettings(group: String): ConsumerSettings[Array[Byte], String] = {
    ConsumerSettings(actorSystem, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(kafkaUrl)
      .withGroupId(group)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET_CONFIG)
  }

  /**
   * Akka flow implementation for kafka
   * @return -> Akka flow
   */
  def flow(): Flow[ProducerMessage.Message[Array[Byte], String, None.type], ProducerMessage.Result[Array[Byte], String, None.type], NotUsed] = {
    Producer.flow(producerSettings)
  }

  /**
   * Akka sink implementation for kafka
   * @return -> Akka sink
   */
  def sink(): Sink[ProducerRecord[Array[Byte], String], Future[Done]] = {
    Producer.plainSink(producerSettings)
  }

  /**
   * Afkka source implementation for kafka
   * @param topic -> Kafka topic to be read
   * @param group -> Group Id of the kafka
   * @return      -> Consumer object wrapped within the Akka source
   */
  def source(topic: String, group: String = UUID.randomUUID().toString): Source[ConsumerRecord[Array[Byte], String], Control] = {
    Consumer.plainSource(consumerSettings(group), Subscriptions.topics(topic))
  }

  val conf: Properties = {
    val prop = new Properties()
    prop.put(StreamsConfig.APPLICATION_ID_CONFIG, configuration.get[String](KAFKA_APPLICATION_ID))
    prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.get[String](KAFKA_URL))
    prop.put(PROP_GROUP_ID, configuration.get[String](KAFKA_GROUP_ID))
    prop.put(VALUE_DESERIALIZER, configuration.get[String](KAFKA_VALUE_DESERIALIZER))
    prop.put(KEY_DESERIALIZER, configuration.get[String](KAFKA_KEY_DESERIALIZER))
    prop
  }

  val consumer = new KafkaConsumer[String, String](conf)

  /* Initialize the conf before use to make it singleton */
  val consumer_poll_timeout =  3000

  /**
   *
   * @param consumer -> Kafka Consumer which is subscribed to a topic
   * @tparam T  -> The type of the message (only simple data types supported, string is ideal preferred)
   * @return    -> Returns the kafka messages that is stored in the topic.
   */
  def getStream[T](consumer: KafkaConsumer[T, T]): List[(T, T)] = {
    val records = consumer.poll(consumer_poll_timeout).asScala
    records.map(rec => {
      (rec.key(), rec.value())
    }).toList
  }
}
