package com.bridgelabz.bookstore.database.mongodb

import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

/**
 * Created on 3/7/2021.
 * Class: MongoConfig.scala
 * Author: Ruchir Dixit
 */
class MongoConfig(uri: String = s"mongodb://${sys.env("MONGOHOST")}:${sys.env("MONGOPORT")}") {
  private val mongoClient: MongoClient = MongoClient(uri)

  def getCollection[T: scala.reflect.ClassTag](collectionName: String,
                                               codecRegistry: CodecRegistry,
                                               databaseName: String):
  MongoCollection[T] = {
    val database: MongoDatabase = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
    database.getCollection[T](collectionName)
  }
}

