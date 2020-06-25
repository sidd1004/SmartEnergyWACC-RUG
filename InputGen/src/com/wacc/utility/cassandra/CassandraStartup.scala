package com.wacc.utility.cassandra

import java.net.InetSocketAddress

import com.datastax.driver.core.{Cluster, Session}

object CassandraStartup {

  /**
   * Initialize the Cassandra Database by using parameters
   * @param port     -> Cassandra Port
   * @param keyspace -> Cassandra Keyspace to use/create
   * @param table     -> Cassandra Table name to use/create
   * @param replicationFactor ->  Replication Factor of the Cassandra Keyspace; default = 1
   * @return  -> Cassandra Cluster object
   */
  def init(uri: Seq[String], port: Int, keyspace: String, table: String, replicationFactor: Int = 1): Session = {
    val c = Cluster.builder().addContactPointsWithPorts(new InetSocketAddress(uri.head, port)).build()
    val session = c.connect()
    session.execute(s"CREATE KEYSPACE IF NOT EXISTS $keyspace WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': '$replicationFactor'};")

    session.execute(s"CREATE TABLE IF NOT EXISTS $keyspace.$table(prosumerID text, timestamp text, values text, PRIMARY KEY (prosumerID, timestamp));")
    c.connect(keyspace)
  }


}

