package com.bridgelabz.bookstore.database.mongodb

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.mongodb.CodecRepository.CodecNames
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import scala.concurrent.Future

class DatabaseCollection2[T: scala.reflect.ClassTag](collectionName: String,
                                                     codecName: CodecNames,
                                                     databaseName: String = sys.env("DATABASE_NAME"),
                                                     mongoDbConfig: MongoConfig = new MongoConfig())
  extends DatabaseCollection[T](collectionName,codecName,databaseName,mongoDbConfig)
    with ICrudRepository[T]{

  /**
   *
   * @param identifier to identify the item in the database
   * @param fieldName that the identifier belongs to
   * @return a sequence of items that have the identifier
   */
  override def read(identifier: Any, fieldName: String): Future[Seq[T]] =
    collection().find(equal(fieldName,identifier)).toFuture()

  /**
   *
   * @param identifier to identify the item in the database
   * @param entity     which should replace object.parameter
   * @param fieldName  that the identifier belongs to
   * @param parameter  field name that has the value to be updated
   * @return any status regarding the update operation
   */
  override def update[U](identifier: Any, entity: U, fieldName: String, parameter: String): Future[Any] =
    collection().updateOne(equal(fieldName, identifier), set(parameter, entity)).toFuture()

}
