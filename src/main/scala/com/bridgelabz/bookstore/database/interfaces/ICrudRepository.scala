package com.bridgelabz.bookstore.database.interfaces

import scala.concurrent.Future

trait ICrudRepository[T] extends ICrud[T] {

  def readByValue(identifier: Any, fieldName: String): Future[Seq[T]]
}
