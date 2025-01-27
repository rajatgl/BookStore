package com.bridgelabz.bookstore.interfaces

import com.bridgelabz.bookstore.models.{WishListItem, WishListProduct}

import scala.concurrent.Future

trait IWishListManager {

  def addItem(userId: String, item: WishListItem): Future[Boolean]

  def getItems(userId: String): Future[Seq[WishListProduct]]

  def removeItem(userId: String, productId: Int): Future[Boolean]

  def addItemToCart(userId: String, productId: Int, quantity: Int): Future[Boolean]
}
