package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.database.interfaces.{ICrud, ICrudRepository}
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, ProductDoesNotExistException}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.bridgelabz.bookstore.models.{Product, User}
/**
 * Created on 3/11/2021.
 * Class: ProductManager.scala
 * Author: Ruchir Dixit.
 */
//class ProductManager(productDatabase : ICrud[Product], userDatabase : ICrud[User]) extends LazyLogging{
class ProductManager(productDatabase : ICrudRepository[Product]) extends LazyLogging{
  /**
   *
   * @param product : product to be added in database
   * @return : Future of true if added successfully or else future of false
   */
  def addProduct(userId: String,product: Product) : Future[Boolean] = {
    productDatabase.findByValue("userId",userId).map(_ => {
          productDatabase.create(product)
          true
      }
    )
  }

  /**
   *
   * @param fieldValue : Field name by which product is to be searched
   * @return : Future of Sequence of product if found or else product not found exception
   */
  def getProduct(fieldValue : Option[String]) : Future[Seq[Product]] = {

    if(fieldValue.isDefined) {
      var doesExist = false
      var productSeq: Seq[Product] = Seq()
      productDatabase.read().map(products => {
        products.foreach(product => {
          if (product.author.equals(fieldValue.get) || product.title.equals(fieldValue.get)) {
            doesExist = true
            productSeq = product.asInstanceOf[Seq[Product]]
          }
        })
        if (doesExist) {
          productSeq
        }
        else {
          throw new ProductDoesNotExistException
        }
      })
    }
    else{
      productDatabase.read()
    }
  }

}
