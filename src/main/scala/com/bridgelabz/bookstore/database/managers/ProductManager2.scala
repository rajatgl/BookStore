package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.models.{Product, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProductManager2(productDatabase : ICrudRepository[Product], userDatabase : ICrudRepository[User])
  extends ProductManager(productDatabase,userDatabase) {

  override def getUserByUserId(userId: String): Future[Option[User]] = {
    userDatabase.readByValue(userId, "userId").map(seq => {
      seq.headOption
    })
  }


}
