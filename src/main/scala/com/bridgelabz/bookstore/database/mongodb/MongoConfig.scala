package com.bridgelabz.bookstore.database.mongodb

import com.typesafe.scalalogging.LazyLogging
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

/**
 * Created on 3/7/2021.
 * Class: MongoConfig.scala
 * Author: Ruchir Dixit
 */
class MongoConfig(uri: String = s"mongodb://${sys.env("MONGO_HOST")}:${sys.env("MONGO_PORT")}") extends LazyLogging{
  logger.info("inside mongo config")
  private val mongoClient: MongoClient = MongoClient(uri)

  /**
   *
   * @param collectionName belonging to the collection that is being fetched
   * @param codecRegistry Codec registry that is being applied on the collection
   * @param databaseName belonging to the database the collection is a part of
   * @tparam T represents the class the objects in the collection belong to
   * @return the MongoCollection that matches the constraints set by the above parameters
   */
  def getCollection[T: scala.reflect.ClassTag](collectionName: String,
                                               codecRegistry: CodecRegistry,
                                               databaseName: String):
  MongoCollection[T] = {
    val database: MongoDatabase = mongoClient.getDatabase(databaseName).withCodecRegistry(codecRegistry)
    database.getCollection[T](collectionName)
  }
}

