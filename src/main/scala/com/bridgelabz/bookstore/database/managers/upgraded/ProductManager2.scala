package com.bridgelabz.bookstore.database.managers.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.ProductManager
import com.bridgelabz.bookstore.models.{Product, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProductManager2(productCollection: ICrudRepository[Product], userCollection: ICrudRepository[User])
  extends ProductManager(productCollection, userCollection) {

  override def getUserByUserId(userId: String): Future[Option[User]] = {
    userCollection.read(userId, "userId").map(seq => {
      seq.headOption
    })
  }

  def helloWorld(): String = "Hello World"
}
