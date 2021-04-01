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

  val taxPercent: Double = System.getenv("TAX_PERCENT").toDouble

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
