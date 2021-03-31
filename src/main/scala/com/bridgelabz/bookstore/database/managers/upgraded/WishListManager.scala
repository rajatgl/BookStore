package com.bridgelabz.bookstore.database.managers.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.interfaces.IWishListManager
import com.bridgelabz.bookstore.models._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class WishListManager(wishListCollection: ICrudRepository[WishList],
                      userCollection: ICrudRepository[User],
                      productCollection: ICrudRepository[Product])

  extends IWishListManager {

  def addItem(userId: String, item: WishListItem): Future[Boolean] = {

    val checks = for {
      user <- verifyUserId(userId)
      items <- getItemsByUserId(userId)
      product <- verifyProduct(item.productId)
    } yield (user, items, product)

    checks.transform {
      case Success(value) =>
        if (value._3.isDefined) {
          if (value._2.isEmpty) {
            wishListCollection.create(WishList(userId, Seq(item)))
          }
          else {
            wishListCollection.update(userId, value._2 :+ item, "userId", "items")
          }
          Try(true)
        }
        else {
          throw new ProductDoesNotExistException
        }

      case Failure(exception) => throw exception
    }

  }

  def getItems(userId: String): Future[Seq[WishListProduct]] = {
    verifyUserId(userId).flatMap(_ => {
      val checks = for {
        wishListItems <- getItemsByUserId(userId)
        products <- productCollection.read()
      } yield (wishListItems, products)

      checks.map(elements => {
        var items = Seq[WishListProduct]()
        elements._1.foreach(item => {
          val product = elements._2.find(product => product.productId.equals(item.productId)).head
          items = items :+ WishListProduct(product, item.timestamp)
        })
        items
      })
    })
  }

  def removeItem(userId: String, productId: Int): Future[Boolean] = {

    val checks = for {
      user <- verifyUserId(userId)
      items <- getItemsByUserId(userId)
    } yield (user, items)

    checks.transform {
      case Success(elements) =>
        if (elements._2.nonEmpty) {
          if (elements._2.exists(item => item.productId == productId)) {
            if (elements._2.size == 1) {
              wishListCollection.delete(userId, "userId")
            }
            else {
              val seq = elements._2.filter(item => item.productId != productId)
              wishListCollection.update(userId, seq, "userId", "items")
            }
            Try(true)
          }
          else {
            throw new ProductDoesNotExistException
          }
        }
        else {
          throw new WishListDoesNotExistException
        }
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
        else {
          throw new UnverifiedAccountException
        }
      }
      else {
        throw new AccountDoesNotExistException
      }
    })
  }

  def verifyProduct(productId: Int): Future[Option[Product]] = productCollection.read(productId, "productId").map(seq => seq.headOption)

  def getItemsByUserId(userId: String): Future[Seq[WishListItem]] = {

    wishListCollection.read(userId, "userId").map(wishLists => {
      if (wishLists.nonEmpty) {
        wishLists.head.items
      }
      else {
        Seq()
      }
    })
  }

}