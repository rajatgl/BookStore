package com.bridgelabz.bookstore

import akka.actor.ActorSystem
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, extractUri, handleExceptions}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.bridgelabz.bookstore.factory.{Collections, DatabaseEnums, DatabaseFactory, Databases}
import com.bridgelabz.bookstore.interfaces.{ICartManager, IProductManager, IUserManager, IWishListManager}
import com.bridgelabz.bookstore.marshallers.OutputMessageJsonSupport
import com.bridgelabz.bookstore.models.{Cart, Otp, OutputMessage, Product, User, WishList}
import com.bridgelabz.bookstore.routes.{CartRoutes, ProductRoutes, UserRoutes, WishListRoutes}
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Created on 3/2/2021.
 * Class: Main.scala
 * Author: Rajat G.L.
 */
object Main extends App with OutputMessageJsonSupport {

  // $COVERAGE-OFF$
  //server configuration variables
  protected val host: String = sys.env("HOST")
  protected val port: Int = sys.env("PORT").toInt


  //actor system and execution context for AkkaHTTP server
  implicit val system: ActorSystem = ActorSystem("Book-Store")
  implicit val executor: ExecutionContext = system.dispatcher

  private val logger = Logger("Main-App")

  //catching Null Pointer Exception and other default Exceptions
  val exceptionHandler = ExceptionHandler {
    case nex: NullPointerException =>
      extractUri { _ =>
        logger.error(nex.getStackTrace.mkString("Array(", ", ", ")"))
        complete(StatusCodes.INTERNAL_SERVER_ERROR.intValue() ->
          OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(),
            "Null value found while parsing the data. Contact the admin."))
      }
    case ex: Exception =>
      extractUri { _ =>
        logger.error(ex.getStackTrace.mkString("Array(", ", ", ")"))
        complete(StatusCodes.INTERNAL_SERVER_ERROR.intValue() ->
          OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(),
            "Some error occurred. Please try again later."))
      }
  }

  val defaultUserManager: IUserManager = DatabaseFactory(DatabaseEnums.MONGODB_USER).asInstanceOf[IUserManager]
  val defaultProductManager : IProductManager = DatabaseFactory(DatabaseEnums.MONGODB_PRODUCT).asInstanceOf[IProductManager]
  val defaultWishListManager : IWishListManager = DatabaseFactory(DatabaseEnums.MONGODB_WISHLIST).asInstanceOf[IWishListManager]
  val defaultCartManager : ICartManager= DatabaseFactory(DatabaseEnums.MONGODB_CART).asInstanceOf[ICartManager]


  def route(userManager: IUserManager,
            productManager: IProductManager,
            wishListManager: IWishListManager,
            cartLisManager: ICartManager): Route = {

    val userRoutes = new UserRoutes(userManager)
    val productRoutes = new ProductRoutes(productManager)
    val wishlistRoutes = new WishListRoutes(wishListManager)
    val cartRoutes = new CartRoutes(cartLisManager)

    handleExceptions(exceptionHandler) {
      Directives.concat(
        //user routes
        userRoutes.loginRoute,
        userRoutes.registerRoute,
        userRoutes.getAddresses,
        userRoutes.addAddressRoute,
        userRoutes.verifyRoute,
        // product routes
        productRoutes.addProductRoute,
        productRoutes.getProductRoute,
        // wishlist routes
        wishlistRoutes.addItem,
        wishlistRoutes.getItems,
        wishlistRoutes.removeItem,
        wishlistRoutes.addItemToCart,
        // cart routes
        cartRoutes.addItem,
        cartRoutes.getItems,
        cartRoutes.removeItem,
        cartRoutes.getPrice
      )
    }
  }

  //binder for the server
  val binder = Http().newServerAt(host, port).bind(route(defaultUserManager,
    defaultProductManager,
    defaultWishListManager,
    defaultCartManager))
  binder.onComplete {
    case Success(serverBinding) => logger.info(s"Listening to ${serverBinding.localAddress}")
    case Failure(error) => logger.error(s"Error : ${error.getMessage}")
  }
}