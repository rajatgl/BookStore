package com.bridgelabz.bookstore.database.interfaces

import scala.concurrent.Future

trait ICrudRepository[T] extends ICrud[T] {

  /**
   *
   * @param identifier to identify the item in the database
   * @param fieldName that the identifier belongs to
   * @return a sequence of items that have the identifier
   */
  def read(identifier: Any, fieldName: String): Future[Seq[T]]

  /**
   *
   * @param identifier to identify the item in the database
   * @param entity which should replace object.parameter
   * @param fieldName that the identifier belongs to
   * @param parameter field name that has the value to be updated
   * @return any status regarding the update operation
   */
  def update[U](identifier: Any, entity: U, fieldName: String, parameter: String): Future[Any]
}
