package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, ProductDoesNotExistException, UnverifiedAccountException}
import com.bridgelabz.bookstore.interfaces.IProductManager
import com.bridgelabz.bookstore.models.{Product, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created on 3/11/2021.
 * Class: ProductManager.scala
 * Author: Ruchir Dixit.
 */
class ProductManager(productCollection : ICrud[Product], userCollection : ICrud[User])
  extends IProductManager{

  /**
   *
   * @param product : product to be added in database
   * @return : Future of true if added successfully or else future of false
   */
  def addProduct(userId: String, product: Product) : Future[Boolean] = {

    getUserByUserId(userId).map(optionalUser => {
      if(optionalUser.isDefined){
        val user = optionalUser.get
        if(user.verificationComplete) {
          productCollection.create(product)
          true
        }
        else{
          throw new UnverifiedAccountException
        }
      }
      else{
        throw new AccountDoesNotExistException
      }
    })
  }

  def getUserByUserId(userId: String): Future[Option[User]] = {
    userCollection.read().map(users => {
      var searchedUser:Option[User] = None
      for (user <- users) {
        if (userId.equals(user.userId)) {
          searchedUser = Some(user)
        }
      }
      searchedUser
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
      productCollection.read().map(products => {
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
      productCollection.read()
    }

  }

}
