package utility.cassandra

import java.net.InetSocketAddress

import com.datastax.driver.core.{Cluster, Session}
import play.api.Configuration
import utility.Constants.Cassandra._
import utility.Delimiters

object CassandraStartup {

  /**
   * Initialize the Cassandra Database by using parameters
   * @param hostname -> Cassandra Hostname
   * @param port     -> Cassandra Port
   * @param keyspace -> Cassandra Keyspace to use/create
   * @param classType -> Keyspace class type to use while creation
   * @param table     -> Cassandra Table name to use/create
   * @param tableSchema -> Schema of the Table to create; if not exist
   * @param replicationFactor ->  Replication Factor of the Cassandra Keyspace; default = 1
   * @return  -> Cassandra Cluster object
   */
  def init(hostname: Seq[String],
           port: Int,
           keyspace: String,
           classType: String,
           table: String,
           tableSchema: String,
           replicationFactor: Int = 1): Cluster = {

    val cluster = Cluster.builder().addContactPointsWithPorts(new InetSocketAddress(hostname.head, port)).build()
    val session = cluster.connect()
    createKeyspaceIfNotExist(session, keyspace, replicationFactor)
    createTableIfNotExist(session, keyspace, table, tableSchema)
    cluster.connect(keyspace)
    cluster
  }

  /**
   * Initialize the Cassandra Database using configuration
   * @param configuration -> Self contained configuration file with all the required Cassandra properties
   * @return              -> Cassandra Cluster Object
   */
  def init(configuration: Configuration): Cluster = {

    val hostname = configuration.get[String](CASSANDRA_HOTS).split(Delimiters.COMMA)
    val port = configuration.get[Int](CASSANDRA_PORT)
    val keyspace = configuration.get[String](CASSANDRA_KEYSPACE)
    val tableName = configuration.get[String](CASSANDRA_TABLE_NAME)
    val tableSchema = configuration.get[String](CASSANDRA_TABLE_SCHEMA)
    val replicationFactor = configuration.get[Int](CASSANDRA_REPLICATION_FACTOR)

    val connection = Cluster.builder().addContactPointsWithPorts(new InetSocketAddress(hostname.head, port)).build()
    val session = connection.connect()
    createKeyspaceIfNotExist(session, keyspace, replicationFactor)
    createTableIfNotExist(session, keyspace, tableName, tableSchema)
    connection.connect(keyspace)
    connection
  }

  def createKeyspaceIfNotExist(session: Session, keySpace: String, replicationFactor: Int = 1): Unit = {
    session.execute(s"CREATE KEYSPACE IF NOT EXISTS $keySpace WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': '2'};")
  }

  def createTableIfNotExist(session: Session, keyspace: String, tableName: String, tableSchema: String): Unit = {
    session.execute(s"CREATE TABLE IF NOT EXISTS $keyspace.$tableName(prosumerID text, timestamp text, values text, PRIMARY KEY (prosumerID, timestamp));")
  }

}

