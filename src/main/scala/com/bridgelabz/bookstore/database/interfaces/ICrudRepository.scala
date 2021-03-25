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
}
