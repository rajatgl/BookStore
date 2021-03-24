package com.bridgelabz.bookstore.database.interfaces

import com.bridgelabz.bookstore.models.Product

import scala.concurrent.Future

trait IProductManager {

  def addProduct(userId: String,product: Product) : Future[Boolean]

  def getProduct(fieldValue : Option[String]) : Future[Seq[Product]]

}
