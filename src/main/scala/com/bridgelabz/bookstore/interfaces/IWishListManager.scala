package com.bridgelabz.bookstore.interfaces

import com.bridgelabz.bookstore.models.WishListItem

import scala.concurrent.Future

trait IWishListManager {

  def addItem(userId: String, item: WishListItem): Future[Boolean]

  def getItems(userId: String): Future[Seq[WishListItem]]

  def removeItem(userId: String, productId: Int): Future[Boolean]

}
