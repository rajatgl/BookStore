package com.bridgelabz.bookstore.database.managers.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, UnverifiedAccountException}
import com.bridgelabz.bookstore.interfaces.IWishListManager
import com.bridgelabz.bookstore.models.{User, WishList, WishListItem}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class WishListManager(wishListCollection: ICrudRepository[WishList],
                      userCollection: ICrudRepository[User])
  extends IWishListManager {

  def addItem(userId: String, item: WishListItem): Future[Boolean] = {

    verifyUserId(userId).transform {

      case Success(_) =>
        val items = getItemsByUserId(userId)
        items.map(seq => {
          if (seq.nonEmpty) {
            wishListCollection.update(userId, seq :+ item, "userId", "items")
          }
          else {
            wishListCollection.create(WishList(userId, seq :+ item))
          }
        })
        Try(true)

      case Failure(exception) => throw exception
    }
  }

  def getItemsByUserId(userId: String): Future[Seq[WishListItem]] = {

    wishListCollection.read(userId, "userId").map(wishLists => {
      if(wishLists.nonEmpty) {
        wishLists.head.items
      }
      else{
        Seq()
      }
    })
  }

  def removeItem(userId: String, productId: Int): Future[Boolean] = {

    verifyUserId(userId).transform {

      case Success(_) =>
        getItemsByUserId(userId).map(wishListItems => {
          val seq = wishListItems
          seq.filter(item => item.product.productId != productId)
          wishListCollection.update(userId, seq, "userId", "items")
        })
        Try(true)

      case Failure(exception) => throw exception
    }
  }

  def getUserByUserId(userId: String): Future[Option[User]] = {
    userCollection.read(userId, "userId").map(seq => {
      seq.headOption
    })
  }

  def verifyUserId(userId: String): Future[Option[User]] = {
    getUserByUserId(userId).map(optionalUser => {

      if (optionalUser.isDefined) {
        if (optionalUser.get.verificationComplete) {
          optionalUser
        }
        else{
          throw new UnverifiedAccountException
        }
      }
      else{
        throw new AccountDoesNotExistException
      }
    })
  }

  override def getItems(userId: String): Future[Seq[WishListItem]] = {
    verifyUserId(userId).flatMap(_ => getItemsByUserId(userId))
  }

}