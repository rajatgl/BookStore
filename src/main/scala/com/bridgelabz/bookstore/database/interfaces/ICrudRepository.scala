package com.bridgelabz.bookstore.database.interfaces

import scala.concurrent.Future

trait ICrudRepository[T] extends ICrud[T] {
  def findById(identifier: Any, fieldName: String): Future[Any]
}
