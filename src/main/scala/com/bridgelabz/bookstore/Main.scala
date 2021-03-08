package com.bridgelabz.bookstore

import akka.actor.ActorSystem
import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, extractUri, handleExceptions}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.marshallers.OutputMessageJsonSupport
import com.bridgelabz.bookstore.models.{Otp, OutputMessage, User}
import com.bridgelabz.bookstore.routes.UserRoutes
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
 * Created on 3/2/2021.
 * Class: Main.scala
 * Author: Rajat G.L.
 */
object Main extends App with OutputMessageJsonSupport {

  //server configuration variables
  protected val host: String = System.getenv("HOST")
  protected val port: Int = System.getenv("PORT").toInt

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
  val userDatabase: ICrud[User] = ???
  val otpDatabase: ICrud[Otp] = ???

  //All managers
  val defaultUserManager: UserManager = new UserManager(userDatabase, otpDatabase)

  def route(userManager: UserManager): Route = {

    val userRoutes = new UserRoutes(userManager)

    handleExceptions(exceptionHandler){
      Directives.concat(

        //user routes
        userRoutes.loginRoute,
        userRoutes.registerRoute,
        userRoutes.getAddresses,
        userRoutes.addAddressRoute
      )
    }
  }

  //binder for the server
  val binder = Http().newServerAt(host, port).bind(route(defaultUserManager))
  binder.onComplete {
    case Success(serverBinding) => logger.info(s"Listening to ${serverBinding.localAddress}")
    case Failure(error) => logger.error(s"Error : ${error.getMessage}")
  }
}
