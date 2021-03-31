package com.bridgelabz.bookstore.routes

import java.util.Date

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, delete, entity, headerValueByName, onComplete, path, pathPrefix, post}
import akka.http.scaladsl.server.{Directives, Route}
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.interfaces.IWishListManager
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.marshallers.{AddProductJsonSupport, OutputMessageJsonSupport, ProductIdJsonSupport, WishListProductJsonSupport, WishlistItemJsonSupport}
import com.bridgelabz.bookstore.models.{OutputMessage, Product, ProductIdModel, WishListItem}
import com.typesafe.scalalogging.Logger

import scala.util.{Failure, Success}

class WishListRoutes(wishListManager: IWishListManager)
  extends AddProductJsonSupport
    with WishlistItemJsonSupport
    with OutputMessageJsonSupport
    with ProductIdJsonSupport
    with WishListProductJsonSupport {

  /**
   *
   * TODO
   * All routes will need token => userId
   *
   * GET /wishlist?wishlistId={id} => Option[WishList]
   * PUT /wishlist => insert a new wishlist or update existing wishlist
   * DELETE /wishlist/product => ability to delete a product from a wishlist {input: wishlistId and productId}
   */

  val logger: Logger = Logger("Wishlist-Route")

  def addItem: Route = post {
    pathPrefix("wishlist") {
      path("item") {
        entity(Directives.as[ProductIdModel]) { request =>
          headerValueByName("Authorization") { token =>

            if (TokenManager.isValidToken(token.split(" ")(1))) {

              val userId = TokenManager.getIdentifier(token.split(" ")(1))
              val timestamp = new Date().getTime
              onComplete(wishListManager.addItem(userId, WishListItem(request.productId, timestamp))) {
                case Success(_) =>
                  complete(StatusCodes.OK.intValue ->
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
                    "Token invalid, addItem to wishlist unsuccessful.")
              )
            }
          }
        }
      }
    }
  }


  def getItems: Route = Directives.get {

    pathPrefix("wishlist") {
      path("items") {
        headerValueByName("Authorization") { token =>
          if (TokenManager.isValidToken(token.split(" ")(1))) {
            val userId = TokenManager.getIdentifier(token.split(" ")(1))

            onComplete(wishListManager.getItems(userId)) {
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
    pathPrefix("wishlist") {
      path("remove") {
        entity(Directives.as[ProductIdModel]) { request =>
          headerValueByName("Authorization") { token =>
            if (TokenManager.isValidToken(token.split(" ")(1))) {
              val userId = TokenManager.getIdentifier(token.split(" ")(1))
              onComplete(wishListManager.removeItem(userId, request.productId)) {
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
                    case wishlistDoesNotExistEx: WishListDoesNotExistException =>
                      complete(wishlistDoesNotExistEx.status() -> OutputMessage(wishlistDoesNotExistEx.status(),
                        wishlistDoesNotExistEx.getMessage))
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
}
