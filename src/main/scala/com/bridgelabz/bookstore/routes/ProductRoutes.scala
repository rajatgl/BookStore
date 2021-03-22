package com.bridgelabz.bookstore.routes

import java.util.Date

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.bridgelabz.bookstore.database.managers.ProductManager
import com.bridgelabz.bookstore.marshallers.{AddProductJsonSupport, OutputMessageJsonSupport}
import akka.http.scaladsl.server.Directives.{complete, entity, get, headerValueByName, onComplete, parameters, path, post}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.models.{OutputMessage, Product}
import com.typesafe.scalalogging.Logger
import akka.http.scaladsl.server.Directives._
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, ProductDoesNotExistException, UnverifiedAccountException}

import scala.util.{Failure, Success}

class ProductRoutes(productManager: ProductManager)
  extends OutputMessageJsonSupport
    with AddProductJsonSupport {

  val logger: Logger = Logger("Product-Routes")

  /**
   * To add products to database
   *
   * @return : If user authorized and product added returns Ok or else returns UnAuthorized
   */
  def addProductRoute: Route = post {
    path("product") {
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
              case Failure(exception) =>
                logger.error(s"Error occurred at ${new Date().getTime}: Exception reads: ${exception.getMessage}")
                exception match {
                  case accountNotFoundEx: AccountDoesNotExistException =>
                    complete(accountNotFoundEx.status() -> OutputMessage(accountNotFoundEx.status(), accountNotFoundEx.getMessage))
                  case unverifiedEx: UnverifiedAccountException =>
                    complete(unverifiedEx.status() -> OutputMessage(unverifiedEx.status(), unverifiedEx.getMessage))
                  case _ =>
                    complete(StatusCodes.INTERNAL_SERVER_ERROR.intValue() -> OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(),
                      "An internal error occurred. Contact the admin."))
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
   *
   * @return : Product if product is found or else returns product not found message
   */
  def getProductRoute: Route = get {
    path("products") {
      parameters('name.?) { name =>
        onComplete(productManager.getProduct(name)) {
          case Success(product) =>
            complete(StatusCodes.OK.intValue() -> product)
          case Failure(exception) =>
            logger.error(s"Error occurred at ${new Date().getTime}: Exception reads: ${exception.getMessage}")
            exception match {
              case productNotFound: ProductDoesNotExistException =>
                complete(productNotFound.status() -> OutputMessage(productNotFound.status(), productNotFound.getMessage))
              case _ =>
                complete(StatusCodes.INTERNAL_SERVER_ERROR.intValue() -> OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(),
                  "An internal error occurred. Contact the admin."))
            }
        }
      }
    }
  }

}
