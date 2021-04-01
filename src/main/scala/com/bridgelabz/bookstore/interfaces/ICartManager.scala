package com.bridgelabz.bookstore.interfaces

import com.bridgelabz.bookstore.models.{CartItem, CartProduct, Price, WishListItem, WishListProduct}

import scala.concurrent.Future

trait ICartManager {

  def addItem(userId: String, item: CartItem): Future[Boolean]

  def getItems(userId: String): Future[Seq[CartProduct]]

  def removeItem(userId: String, productId: Int): Future[Boolean]

  def getPrice(userId: String): Future[Price]
}
