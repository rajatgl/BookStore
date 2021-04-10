package com.bridgelabz.bookstore.database.managers.upgraded

import java.util.Date

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.interfaces.IOrderManager
import com.bridgelabz.bookstore.managers.EmailManager
import com.bridgelabz.bookstore.models.{Cart, CartItem, Order, Product, User}
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrderManager(orderCollection: ICrudRepository[Order],
                   userCollection: ICrudRepository[User],
                   cartCollection: ICrudRepository[Cart],
                   productCollection: ICrudRepository[Product]) extends IOrderManager {

  val logger: Logger = Logger("Order-Manager")

  def placeOrder(userId: String, transactionId: String, deliveryIndex: Int): Future[String] = {

    val checks = for {
      user <- verifyUserId(userId)
      cart <- cartCollection.read(userId, "userId")
      products <- productCollection.read()
    } yield (user, cart, products)

    checks.flatMap(elements => {

      if (deliveryIndex >= elements._1.get.addresses.length) {
        logger.warn(s"Attempted to access deliveryIndex: ${deliveryIndex} at ${new Date().getTime}")
        throw new AddressNotFoundException
      }
      var orderItems = Seq[CartItem]()
      if (elements._2.nonEmpty) {
        for (item <- elements._2.head.items) {
          val productOp = elements._3.find(product => product.productId == item.productId)
          if (productOp.isDefined) {
            if (productOp.get.quantity >= item.quantity) {
              productCollection.update(item.productId, productOp.get.quantity - item.quantity, "productId", "quantity")
              logger.info(s"current quantity: ${productOp.get.quantity - item.quantity} for product: ${item.productId} updated at: ${new Date().getTime}")
              orderItems = orderItems :+ item
            }
            else {
              Future.failed(throw new ProductQuantityUnavailableException)
            }
          }
          else {
            Future.failed(throw new ProductDoesNotExistException)
          }
        }

        // TODO: find a way of generating a unique order ID
        // TODO: find a way of dynamically creating the delivery timestamp: represents expected delivery
        val address = elements._1.get.addresses(deliveryIndex)
        val order = Order(userId, transactionId, transactionId,
          address, orderItems, "Order Placed", new Date().getTime,
          new Date().getTime + (5 * 24 * 60 * 60 * 1000))

        orderCollection.create(order).map(_ => {
          EmailManager.sendEmail(elements._1.get.email, "Order Placed", s"Your order ID is ${order.orderId}. Happy to serve you!")
          logger.info(s"order: ${order.orderId} is created at: ${new Date().getTime}")
          order.orderId
        })
      }
      else {
        Future.failed(throw new CartDoesNotExistException)
      }
    })

  }

  def getOrders(userId: String, orderId: Option[String]): Future[Seq[Order]] = {

    verifyUserId(userId).flatMap(_ =>

      if (orderId.isDefined) {
        orderCollection.read(orderId, "orderId")
      }
      else {
        orderCollection.read(userId, "userId")
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
   * @param productId to be searched for in the database
   * @return the product who matches the search and if not found then None
   */
  def verifyProduct(productId: Int): Future[Option[Product]] =
    productCollection.read(productId, "productId").map(seq => seq.headOption)

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

}
