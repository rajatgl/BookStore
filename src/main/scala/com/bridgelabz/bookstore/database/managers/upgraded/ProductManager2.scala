package com.bridgelabz.bookstore.database.managers.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.ProductManager
import com.bridgelabz.bookstore.models.{Product, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ProductManager2(productCollection: ICrudRepository[Product], userCollection: ICrudRepository[User])
  extends ProductManager(productCollection, userCollection) {


  /**
   *
   * @param userId to be searched for in the database
   * @return the user who matches the search and if not found then None
   */
  override def getUserByUserId(userId: String): Future[Option[User]] = {
    userCollection.read(userId, "userId").map(seq => {
      seq.headOption
    })
  }
}
