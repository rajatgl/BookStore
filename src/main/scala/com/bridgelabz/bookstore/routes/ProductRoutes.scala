package com.bridgelabz.bookstore.routes

import java.util.Date

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.bridgelabz.bookstore.database.managers.ProductManager
import com.bridgelabz.bookstore.marshallers.{AddProductJsonSupport, OutputMessageJsonSupport}
import akka.http.scaladsl.server.Directives.{complete, entity, get, headerValueByName, onComplete, parameters, path, post}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.models.{OutputMessage, Product}
import com.typesafe.scalalogging.LazyLogging
import akka.http.scaladsl.server.Directives._
import com.bridgelabz.bookstore.exceptions.ProductDoesNotExistException

import scala.util.{Failure, Success}

class ProductRoutes(productManager: ProductManager) extends OutputMessageJsonSupport with AddProductJsonSupport with LazyLogging {

  /**
   * To add products to database
   * @return : If user authorized and product added returns Ok or else returns UnAuthorized
   */
  def addProductRoute: Route = post {
    path("addProduct") {
      entity(Directives.as[Product]) { request =>
        headerValueByName("Authorization") { token =>
          if (TokenManager.isValidToken(token.split(" ")(1))) {
            val userId = TokenManager.getIdentifier(token.split(" ")(1))
            onComplete(productManager.addProduct(userId, request)) {
              case Success(value) =>
                if (value) {
                  complete(StatusCodes.OK.intValue() -> OutputMessage(StatusCodes.OK.intValue(),
                    "Product added successfully."))
                } else {
                  complete(StatusCodes.INTERNAL_SERVER_ERROR.intValue() -> OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(),
                    "Product could not be added."))
                }
            }
          }
          else {
            logger.warn(s"Invalid token use attempted at ${new Date().getTime}")
            complete(
              StatusCodes.UNAUTHORIZED.intValue() ->
                OutputMessage(StatusCodes.UNAUTHORIZED.intValue(), "Token invalid, login unsuccessful.")
            )
          }
        }
      }
    }
  }

  /**
   * To get Seq of Products using title or author
   * @return : Product if product is found or else returns product not found message
   */
  def getProductRoute : Route = get {
    path("products") {
      parameters('name.as[String]) {
        (name) =>
            onComplete(productManager.getProduct(name)){
              case Success(product) =>
                complete(OutputMessage(StatusCodes.OK.intValue(),product.toString()))
              case Failure(exception) =>
                exception match {
                  case productNotFound : ProductDoesNotExistException =>
                    complete(productNotFound.status() -> OutputMessage(productNotFound.status(),productNotFound.getMessage))
                  case _ =>
                    complete(StatusCodes.INTERNAL_SERVER_ERROR.intValue() -> OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(),
                      "An internal error occurred. Contact the admin."))
                }
            }
      }
    }
  }

}
