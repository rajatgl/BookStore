package com.bridgelabz.bookstore.database.mongodb

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.mongodb.CodecRepository.CodecNames
import org.mongodb.scala.model.Filters.equal
import scala.concurrent.Future

class DatabaseCollection2[T: scala.reflect.ClassTag](collectionName: String,
                                                     codecName: CodecNames,
                                                     databaseName: String = sys.env("DATABASE_NAME"),
                                                     mongoDbConfig: MongoConfig = new MongoConfig())
  extends DatabaseCollection[T](collectionName,codecName,databaseName,mongoDbConfig)
    with ICrudRepository[T]{

  override def findById(identifier: Any, fieldName: String): Future[Seq[T]] = collection().find(equal(fieldName,identifier)).toFuture()

}
