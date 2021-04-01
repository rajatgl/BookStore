package com.bridgelabz.bookstore.routes

import java.util.Date

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, delete, entity, headerValueByName, onComplete, path, pathPrefix, post}
import akka.http.scaladsl.server.{Directives, Route}
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.interfaces.ICartManager
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.marshallers._
import com.bridgelabz.bookstore.models.{CartItem, CartProductIdModel, OutputMessage, ProductIdModel}
import com.typesafe.scalalogging.Logger

import scala.util.{Failure, Success}

class CartRoutes(cartManager: ICartManager)
  extends AddProductJsonSupport
    with OutputMessageJsonSupport
    with ProductIdJsonSupport
    with CartProductIdJsonSupport
    with CartProductJsonSupport
    with PriceJsonSupport {

  val logger: Logger = Logger("Wishlist-Route")

  def addItem: Route = post {
    pathPrefix("cart") {
      path("item") {
        entity(Directives.as[CartProductIdModel]) { request =>
          headerValueByName("Authorization") { token =>
            if (TokenManager.isValidToken(token.split(" ")(1))) {
              val userId = TokenManager.getIdentifier(token.split(" ")(1))
              val timestamp = new Date().getTime
              onComplete(cartManager.addItem(userId, CartItem(request.productId, timestamp, request.quantity))) {
                case Success(_) => complete(StatusCodes.OK.intValue ->
                  OutputMessage(StatusCodes.OK.intValue,
                    "Item added successfully."))
                case Failure(exception) =>
                  logger.error(s"Error occurred at ${new Date().getTime}: Exception reads: ${exception.getMessage}")
                  exception match {
                    case accountNotFoundEx: AccountDoesNotExistException =>
                      complete(accountNotFoundEx.status() ->
                        OutputMessage(accountNotFoundEx.status(), accountNotFoundEx.getMessage))
                    case unverifiedEx: UnverifiedAccountException =>
                      complete(unverifiedEx.status() ->
                        OutputMessage(unverifiedEx.status(), unverifiedEx.getMessage))
                    case productDoesNotExistEx: ProductDoesNotExistException =>
                      complete(productDoesNotExistEx.status() ->
                        OutputMessage(productDoesNotExistEx.status(),
                          productDoesNotExistEx.getMessage))
                    case productQuantityUnavailableEx: ProductQuantityUnavailableException =>
                      complete(productQuantityUnavailableEx.status() ->
                        OutputMessage(productQuantityUnavailableEx.status(),
                          productQuantityUnavailableEx.getMessage))
                    case _ =>
                      complete(StatusCodes.InternalServerError.intValue ->
                        OutputMessage(StatusCodes.InternalServerError.intValue,
                          "An internal error occurred. Contact the admin."))
                  }
              }
            }
            else {
              logger.warn(s"Invalid token use attempted at ${new Date().getTime}")
              complete(StatusCodes.Unauthorized.intValue ->
                OutputMessage(StatusCodes.Unauthorized.intValue,
                  "Token invalid, addItem to wishlist unsuccessful."))
            }
          }
        }
      }
    }
  }


  def getItems: Route = Directives.get {

    pathPrefix("cart") {
      path("items") {
        headerValueByName("Authorization") { token =>
          if (TokenManager.isValidToken(token.split(" ")(1))) {
            val userId = TokenManager.getIdentifier(token.split(" ")(1))

            onComplete(cartManager.getItems(userId)) {
              case Success(value) =>
                complete(StatusCodes.OK.intValue -> value)
              case Failure(exception) =>
                logger.error(s"Error occurred at ${new Date().getTime}: Exception reads: ${exception.getMessage}")
                exception match {
                  case accountNotFoundEx: AccountDoesNotExistException =>
                    complete(accountNotFoundEx.status() ->
                      OutputMessage(accountNotFoundEx.status(), accountNotFoundEx.getMessage))
                  case unverifiedEx: UnverifiedAccountException =>
                    complete(unverifiedEx.status() ->
                      OutputMessage(unverifiedEx.status(), unverifiedEx.getMessage))
                  case _ =>
                    complete(StatusCodes.InternalServerError.intValue ->
                      OutputMessage(StatusCodes.InternalServerError.intValue,
                        "An internal error occurred. Contact the admin."))
                }
            }
          }
          else {

            logger.warn(s"Invalid token use attempted at ${new Date().getTime}")
            complete(
              StatusCodes.Unauthorized.intValue ->
                OutputMessage(StatusCodes.Unauthorized.intValue,
                  "Token invalid, get items from wishlist unsuccessful.")
            )
          }
        }
      }
    }
  }

  def removeItem: Route = delete {
    pathPrefix("cart") {
      path("remove") {
        entity(Directives.as[ProductIdModel]) { request =>
          headerValueByName("Authorization") { token =>
            if (TokenManager.isValidToken(token.split(" ")(1))) {
              val userId = TokenManager.getIdentifier(token.split(" ")(1))
              onComplete(cartManager.removeItem(userId, request.productId)) {
                case Success(_) =>
                  complete(StatusCodes.OK.intValue -> OutputMessage(StatusCodes.OK.intValue,
                    "Item removed successfully."))
                case Failure(exception) =>
                  logger.error(s"Error occurred at ${new Date().getTime}: Exception reads: ${exception.getMessage}")
                  exception match {
                    case accountNotFoundEx: AccountDoesNotExistException => complete(accountNotFoundEx.status() ->
                      OutputMessage(accountNotFoundEx.status(), accountNotFoundEx.getMessage))
                    case unverifiedEx: UnverifiedAccountException => complete(unverifiedEx.status() ->
                      OutputMessage(unverifiedEx.status(), unverifiedEx.getMessage))
                    case productDoesNotExistEx: ProductDoesNotExistException => complete(productDoesNotExistEx.status() ->
                      OutputMessage(productDoesNotExistEx.status(), productDoesNotExistEx.getMessage))
                    case cartDoesNotExistEx: CartDoesNotExistException =>
                      complete(cartDoesNotExistEx.status() -> OutputMessage(cartDoesNotExistEx.status(),
                        cartDoesNotExistEx.getMessage))
                    case _ =>
                      complete(StatusCodes.InternalServerError.intValue -> OutputMessage(StatusCodes.InternalServerError.intValue,
                        "An internal error occurred. Contact the admin."))
                  }
              }
            }
            else {
              logger.warn(s"Invalid token use attempted at ${new Date().getTime}")
              complete(StatusCodes.Unauthorized.intValue -> OutputMessage(StatusCodes.Unauthorized.intValue,
                "Token invalid, addItem to wishlist unsuccessful.")
              )
            }
          }
        }
      }
    }
  }

  def getPrice: Route = Directives.get {
    pathPrefix("cart") {
      path("price") {
        headerValueByName("Authorization") { token =>
          if (TokenManager.isValidToken(token.split(" ")(1))) {
            val userId = TokenManager.getIdentifier(token.split(" ")(1))

            onComplete(cartManager.getPrice(userId)) {
              case Success(value) =>
                complete(StatusCodes.OK.intValue -> value)
              case Failure(exception) =>
                logger.error(s"Error occurred at ${new Date().getTime}: Exception reads: ${exception.getMessage}")
                exception match {
                  case accountNotFoundEx: AccountDoesNotExistException =>
                    complete(accountNotFoundEx.status() ->
                      OutputMessage(accountNotFoundEx.status(), accountNotFoundEx.getMessage))
                  case unverifiedEx: UnverifiedAccountException =>
                    complete(unverifiedEx.status() ->
                      OutputMessage(unverifiedEx.status(), unverifiedEx.getMessage))
                  case _ =>
                    complete(StatusCodes.InternalServerError.intValue ->
                      OutputMessage(StatusCodes.InternalServerError.intValue,
                        "An internal error occurred. Contact the admin."))
                }
            }
          }
          else {
            logger.warn(s"Invalid token use attempted at ${new Date().getTime}")
            complete(
              StatusCodes.Unauthorized.intValue ->
                OutputMessage(StatusCodes.Unauthorized.intValue,
                  "Token invalid, get items from wishlist unsuccessful.")
            )
          }
        }
      }
    }
  }
}
