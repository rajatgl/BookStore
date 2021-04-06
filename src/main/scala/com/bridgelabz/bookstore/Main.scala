package com.bridgelabz.bookstore

import akka.actor.ActorSystem
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, extractUri, handleExceptions}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.upgraded.{CartManager, ProductManager2, UserManager2, WishListManager}
import com.bridgelabz.bookstore.factory.{Collections, DatabaseFactory, Databases}
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

  val userCollection: ICrudRepository[User] = DatabaseFactory[User](Collections.USER, Databases.MONGODB)
  val otpCollection: ICrudRepository[Otp] = DatabaseFactory[Otp](Collections.OTP, Databases.MONGODB)
  val productCollection: ICrudRepository[Product] = DatabaseFactory[Product](Collections.PRODUCT, Databases.MONGODB)
  val wishListCollection: ICrudRepository[WishList] = DatabaseFactory[WishList](Collections.WISHLIST, Databases.MONGODB)
  val cartCollection: ICrudRepository[Cart] = DatabaseFactory[Cart](Collections.CART, Databases.MONGODB)

  val defaultUserManager: IUserManager = new UserManager2(
    userCollection,
    otpCollection
  )

  val defaultProductManager: IProductManager = new ProductManager2(
    productCollection,
    userCollection
  )

  val defaultWishListManager: IWishListManager = new WishListManager(
    wishListCollection,
    userCollection,
    productCollection,
    cartCollection
  )

  val defaultCartManager: ICartManager = new CartManager(
    cartCollection,
    userCollection,
    productCollection
  )

  /**
   *
   * @param userManager which manages the connection between routes and user collection/ table
   * @param productManager which manages the connection between routes and product collection/ table
   * @param wishListManager which manages the connection between routes and wishlist collection/ table
   * @param cartLisManager which manages the connection between routes and cart collection/ table
   * @return the Route object that can be bound to the server
   */
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