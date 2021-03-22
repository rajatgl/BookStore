package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, ProductDoesNotExistException, UnverifiedAccountException}
import com.bridgelabz.bookstore.models.{Product, User}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created on 3/11/2021.
 * Class: ProductManager.scala
 * Author: Ruchir Dixit.
 */
class ProductManager(productDatabase : ICrud[Product], userDatabase : ICrud[User]) extends LazyLogging{

  /**
   *
   * @param product : product to be added in database
   * @return : Future of true if added successfully or else future of false
   */
  def addProduct(userId: String,product: Product) : Future[Boolean] = {
    var isExist = false
    userDatabase.read().map(users => {
      for (user <- users) {
        if (userId.equals(user.userId)) {
          if(user.verificationComplete) {
            isExist = true
          }
          else{
            throw new UnverifiedAccountException
          }
        }
      }
      if(isExist){
        productDatabase.create(product)
        true
      }
      else {
        throw new AccountDoesNotExistException
      }
    })
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
          if (product.author.toLowerCase.contains(fieldValue.get.toLowerCase) || product.title.toLowerCase.contains(fieldValue.get.toLowerCase)) {
            doesExist = true
            productSeq = productSeq :+ product
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
