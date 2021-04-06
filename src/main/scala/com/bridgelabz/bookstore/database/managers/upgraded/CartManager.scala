package com.bridgelabz.bookstore.database.managers.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.interfaces.ICartManager
import com.bridgelabz.bookstore.models._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class CartManager(cartCollection: ICrudRepository[Cart],
                  userCollection: ICrudRepository[User],
                  productCollection: ICrudRepository[Product]) extends ICartManager {

  //Tax percent to be used for bill calculation
  val taxPercent: Double = sys.env("TAX_PERCENT").toDouble

  /**
   *
   * @param userId belonging to the user who is adding an item to the cart
   * @param item that is being added to the cart
   * @return future of true/false depending on the success of the addition operation
   */
  override def addItem(userId: String, item: CartItem): Future[Boolean] = {
    val checks = for {
      user <- verifyUserId(userId)
      items <- getItemsByUserId(userId)
      product <- verifyProduct(item.productId)
    } yield (user, items, product)

    checks.transform {
      case Success(value) =>
        if (value._3.isDefined) {
          if(item.quantity <= value._3.get.quantity) {
            if (value._2.isEmpty) {
              cartCollection.create(Cart(userId, Seq(item)))
            }
            else {
              cartCollection.update(userId, value._2 :+ item, "userId", "items")
            }
            Try(true)
          }
          else {
            throw new ProductQuantityUnavailableException
          }
        }
        else {
          throw new ProductDoesNotExistException
        }

      case Failure(exception) => throw exception
    }
  }

  /**
   *
   * @param userId belonging to the user who's cart items are to be fetched
   * @return a list of Cart Products belonging to the user
   */
  override def getItems(userId: String): Future[Seq[CartProduct]] = {

    verifyUserId(userId).flatMap(_ => {
      val checks = for {
        cartItems <- getItemsByUserId(userId)
        products <- productCollection.read()
      } yield (cartItems, products)

      checks.map(elements => {
        var items = Seq[CartProduct]()
        elements._1.foreach(item => {
          val product = elements._2.find(product => product.productId.equals(item.productId))
          if(product.isDefined) {
            items = items :+ CartProduct(product.get, item.timestamp, item.quantity)
          }
        })
        items
      })
    })
  }

  /**
   *
   * @param userId belonging to the user who is removing an item from the cart
   * @param productId of the product that is being removed from the cart
   * @return future of true/ false depending on the status of the deletion operation
   */
  override def removeItem(userId: String, productId: Int): Future[Boolean] = {
    val checks = for {
      user <- verifyUserId(userId)
      items <- getItemsByUserId(userId)
    } yield (user, items)

    checks.transform {
      case Success(elements) =>
        if (elements._2.nonEmpty) {
          if (elements._2.exists(item => item.productId == productId)) {
            if (elements._2.size == 1) {
              cartCollection.delete(userId, "userId")
            }
            else {
              val seq = elements._2.filter(item => item.productId != productId)
              cartCollection.update(userId, seq, "userId", "items")
            }
            Try(true)
          }
          else {
            throw new ProductDoesNotExistException
          }
        }
        else {
          throw new CartDoesNotExistException
        }
      case Failure(exception) => throw exception
    }
  }

  /**
   *
   * @param userId who is fetching the cart price
   * @return a Price object representing the cart's bill along with added tax
   */
  override def getPrice(userId: String): Future[Price] = {
    getItems(userId).map(items => {

      var totalBill: Double = 0f
      var grandTotal: Double = 0f
      var totalTax: Double = 0f

      items.foreach(item => totalBill += (item.product.price * item.quantity))
      totalTax = (totalBill * taxPercent)/100
      grandTotal = totalBill + totalTax

      Price(totalBill, totalTax, grandTotal)
    })
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
  def getItemsByUserId(userId: String): Future[Seq[CartItem]] = {

    cartCollection.read(userId, "userId").map(carts => {
      if (carts.nonEmpty) {
        carts.head.items
      }
      else {
        Seq()
      }
    })
  }
}
