package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.exceptions.ProductDoesNotExistException
import com.bridgelabz.bookstore.models.ProductCase
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class ProductManager(productDatabase : ICrud[ProductCase]) extends LazyLogging{

  /**
   *
   * @param product : product to be added in database
   * @return : Future of true if added successfully or else future of false
   */
  def addProduct(product: ProductCase) : Future[Boolean] = {
    productDatabase.create(product).transform({
      case Success(_) => Success(true)
      case Failure(_) => Success(false)
    })
  }

  /**
   *
   * @param fieldName : Field name by which product is to be searched
   * @return : Future of product if found or else product not found exception
   */
  def getProduct(fieldName : String) : Future[ProductCase] = {
    var doesExist = false
    var productFound : ProductCase = null
    productDatabase.read().map(products => {
      products.foreach(product => {
        if(product.author.equals(fieldName) || product.title.equals(fieldName)){
          doesExist = true
          productFound = product
        }
      })
      if(doesExist){
        productFound
      }
      else {
        throw new ProductDoesNotExistException
      }
    })
  }

}
