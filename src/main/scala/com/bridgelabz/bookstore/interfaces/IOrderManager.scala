package com.bridgelabz.bookstore.interfaces

import com.bridgelabz.bookstore.models.Order

import scala.concurrent.Future

trait IOrderManager {

  def placeOrder(userId: String, transactionId: String, deliveryIndex: Int): Future[String]

  def getOrders(userId: String, orderId: Option[String]): Future[Seq[Order]]

}
