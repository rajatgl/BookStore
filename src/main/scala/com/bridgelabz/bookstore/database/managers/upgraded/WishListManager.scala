package com.bridgelabz.bookstore.database.managers.upgraded

import java.util.Date

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.interfaces.IWishListManager
import com.bridgelabz.bookstore.models._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class WishListManager(wishListCollection: ICrudRepository[WishList],
                      userCollection: ICrudRepository[User],
                      productCollection: ICrudRepository[Product],
                      cartCollection: ICrudRepository[Cart])

  extends IWishListManager {

  /**
   *
   * @param userId belonging to the user who is adding an item to the wishlist
   * @param item that is being added to the cart
   * @return future of true/false depending on the success of the addition operation
   */
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

  /**
   *
   * @param userId belonging to the user who's wishlist items are to be fetched
   * @return a list of Wishlist Products belonging to the user
   */
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

  /**
   *
   * @param userId belonging to the user who is removing an item from the wishlist
   * @param productId of the product that is being removed from the wishlist
   * @return future of true/ false depending on the status of the deletion operation
   */
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

  /**
   *
   * @param userId belonging to the user who is shifting a wishlist item to his/her cart
   * @param productId belonging to the product that is being shifted from the wishlist to the cart
   * @param quantity chosen for the product that is being added to the cart
   * @return future of ture/false depending on the status of the shifting operation
   */
  def addItemToCart(userId: String, productId: Int, quantity: Int): Future[Boolean] = {
    val checks = for {
      user <- verifyUserId(userId)
      items <- getItemsByUserId(userId)
      carts <- cartCollection.read(userId, "userId")
      product <- productCollection.read(productId, "productId")
    } yield (user, items, carts, product)

    checks.transform {
      case Success(elements) =>
        if (elements._2.nonEmpty) {
          if (elements._2.exists(item => item.productId == productId)) {

            //add item to cart
            if(elements._4.nonEmpty && elements._4.head.quantity >= quantity) {
              if (elements._3.nonEmpty) {
                val newCartItems = elements._3.head.items :+ CartItem(productId, new Date().getTime, quantity)
                cartCollection.update(userId, newCartItems, "userId", "items")
              }
              else {
                val cart = Cart(userId, Seq(CartItem(productId, new Date().getTime, quantity)))
                cartCollection.create(cart)
              }
              Try(true)
            }
            else{
              throw new ProductQuantityUnavailableException
            }
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

  /**
   *
   * @param userId to be searched for in the database
   * @return the user who matches the search and if not found then None
   */
  def getUserByUserId(userId: String): Future[Option[User]] = {
    userCollection.read(userId, "userId").map(seq => {
      seq.headOption
    })
  }

  /**
   *
   * @param userId to be searched for in the database and is verified
   * @return the user who matches the search [is verified] and if not found then None
   */
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

  /**
   *
   * @param productId to be searched for in the database
   * @return the product who matches the search and if not found then None
   */
  def verifyProduct(productId: Int): Future[Option[Product]] = productCollection.read(productId, "productId").map(seq => seq.headOption)

  /**
   *
   * @param userId who's cart items are to be searched for in the database
   * @return the cart items that matches the search and if not found then empty sequence
   */
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