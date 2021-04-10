package com.bridgelabz.bookstore.routes

import java.util.Date

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{_symbol2NR, complete, entity, headerValueByName, onComplete, parameters, path, post}
import akka.http.scaladsl.server.{Directives, Route}
import com.bridgelabz.bookstore.exceptions.IBookStoreException
import com.bridgelabz.bookstore.interfaces.IOrderManager
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.marshallers.{OrderJsonSupport, OutputMessageJsonSupport, PlaceOrderJsonSupport}
import com.bridgelabz.bookstore.models.{OutputMessage, PlaceOrderModel}
import com.typesafe.scalalogging.Logger

import scala.util.{Failure, Success}

class OrderRoutes(orderManager: IOrderManager)
  extends OrderJsonSupport
    with OutputMessageJsonSupport
    with PlaceOrderJsonSupport {

  val logger: Logger = Logger("Order-Route")

  def placeOrder: Route = post {
    path("order") {
      entity(Directives.as[PlaceOrderModel]) { request =>
        headerValueByName("Authorization") { token =>
          if (TokenManager.isValidToken(token.split(" ")(1))) {
            val userId = TokenManager.getIdentifier(token.split(" ")(1))
            onComplete(orderManager.placeOrder(userId, request.transactionId, request.deliveryAddressIndex)) {
              case Success(_) => complete(StatusCodes.OK.intValue ->
                OutputMessage(StatusCodes.OK.intValue,
                  "order placed successfully."))
              case Failure(exception) =>
                logger.error(s"Error occurred at ${new Date().getTime}: Exception reads: ${exception.getMessage}")
                exception match {
                  case exception: IBookStoreException =>
                    complete(exception.status() ->
                      OutputMessage(exception.status(),
                        exception.getMessage))
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
                "Token invalid, place order unsuccessful."))
          }
        }
      }
    }
  }


  def getOrders: Route = Directives.get {

    path("orders") {
      parameters('orderId.?){ orderId =>
        headerValueByName("Authorization") { token =>
          if (TokenManager.isValidToken(token.split(" ")(1))) {
            val userId = TokenManager.getIdentifier(token.split(" ")(1))

            onComplete(orderManager.getOrders(userId,orderId)) {
              case Success(value) =>
                complete(StatusCodes.OK.intValue -> value)
              case Failure(exception) =>
                logger.error(s"Error occurred at ${new Date().getTime}: Exception reads: ${exception.getMessage}")
                exception match {
                  case exception: IBookStoreException =>
                    complete(exception.status() ->
                      OutputMessage(exception.status(),
                        exception.getMessage))
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
