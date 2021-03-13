package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, ProductDoesNotExistException}
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import com.bridgelabz.bookstore.models.{Product, User}

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
          isExist = true
        }
      }
    })
    if(isExist){
      productDatabase.create(product).transform({
        case Success(_) => Success(true)
        case Failure(_) => Success(false)
      })
    }
    else {
      throw new AccountDoesNotExistException
    }
  }

  /**
   *
   * @param fieldName : Field name by which product is to be searched
   * @return : Future of Sequence of product if found or else product not found exception
   */
  def getProduct(fieldName : String) : Future[Seq[Product]] = {
    var doesExist = false
    var productSeq: Seq[Product] = Seq()
    productDatabase.read().map(products => {
      products.foreach(product => {
        if(product.author.equals(fieldName) || product.title.equals(fieldName)){
          doesExist = true
          productSeq = product.asInstanceOf[Seq[Product]]
        }
      })
      if(doesExist){
        productSeq
      }
      else {
        throw new ProductDoesNotExistException
      }
    })
  }

}
