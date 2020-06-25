package com.wacc.utility.mongodb

import com.typesafe.config.Config
import com.wacc.utility.Constants.Mongo._
import com.wacc.utility.mongodb.Helpers._
import org.mongodb.scala._

object MongoDbUtil {

  def getListOfUsers(config: Config): Seq[String] = {

    val mongoClient: MongoClient = MongoClient(config.getString(MONGO_URI))

    // get handle to "mydb" database
    val database: MongoDatabase = mongoClient.getDatabase(config.getString(MONGO_DB))

    // get a handle to the "test" collection
    val collection: MongoCollection[Document] = database.getCollection(USERS_COLLECTION)

    val res = collection.find().extractIds()
    res.foreach(println)
    res
  }
}


