package com.bridgelabz.bookstore

import akka.actor.ActorSystem
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, extractUri, handleExceptions}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.{ProductManager, UserManager}
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection2}
import com.bridgelabz.bookstore.database.mysql.tables.upgraded.{ProductTable2, UserTable2}
import com.bridgelabz.bookstore.interfaces.{IProductManager, IUserManager}
import com.bridgelabz.bookstore.marshallers.OutputMessageJsonSupport
import com.bridgelabz.bookstore.models.{Otp, OutputMessage, Product, User}
import com.bridgelabz.bookstore.routes.{ProductRoutes, UserRoutes}
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
          OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(), "Null value found while parsing the data. Contact the admin."))
      }
    case ex: Exception =>
      extractUri { _ =>
        logger.error(ex.getStackTrace.mkString("Array(", ", ", ")"))
        complete(StatusCodes.INTERNAL_SERVER_ERROR.intValue() ->
          OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(), "Some error occurred. Please try again later."))
      }
  }

  //All databases
  //val userCollection: ICrudRepository[User] = new DatabaseCollection2[User]("users",CodecRepository.USER)
  val userCollection: ICrudRepository[User] = new UserTable2("users")
  val otpCollection: ICrudRepository[Otp] = new DatabaseCollection2[Otp]("userOtp",CodecRepository.OTP)
  //val productCollection: ICrudRepository[Product] = new DatabaseCollection2[Product]("products",CodecRepository.PRODUCT)
  val productCollection: ICrudRepository[Product] = new ProductTable2("products")


  //All managers
  val defaultUserManager: IUserManager = new UserManager(userCollection, otpCollection)
  val defaultProductManager: IProductManager = new ProductManager(productCollection,userCollection)

  def route(userManager: IUserManager, productManager: IProductManager): Route = {

    val userRoutes = new UserRoutes(userManager)
    val productRoutes = new ProductRoutes(productManager)

    handleExceptions(exceptionHandler){
      Directives.concat(
        //user routes
        userRoutes.loginRoute,
        userRoutes.registerRoute,
        userRoutes.getAddresses,
        userRoutes.addAddressRoute,
        userRoutes.verifyRoute,
        // product routes
        productRoutes.addProductRoute,
        productRoutes.getProductRoute
      )
    }
  }

  //binder for the server
  val binder = Http().newServerAt(host, port).bind(route(defaultUserManager,defaultProductManager))
  binder.onComplete {
    case Success(serverBinding) => logger.info(s"Listening to ${serverBinding.localAddress}")
    case Failure(error) => logger.error(s"Error : ${error.getMessage}")
  }
}
