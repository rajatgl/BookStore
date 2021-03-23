package com.bridgelabz.bookstore.routes

import java.util.Date

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives.{complete, entity, headerValueByName, onComplete, parameters, path, post, respondWithHeaders}
import akka.http.scaladsl.server.{Directives, Route}
import com.bridgelabz.bookstore.database.interfaces.IUserManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, BadEmailPatternException, PasswordMismatchException, UnverifiedAccountException}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.marshallers.{AddAddressJsonSupport, LoginJsonSupport, OutputMessageJsonSupport, RegisterJsonSupport}
import com.bridgelabz.bookstore.models._
import com.typesafe.scalalogging.Logger

import scala.concurrent.Future
import scala.util.{Failure, Success}

class UserRoutes(userManager: IUserManager)
  extends RegisterJsonSupport
    with OutputMessageJsonSupport
    with LoginJsonSupport
    with AddAddressJsonSupport {

  val logger: Logger = Logger("User-Routes")

  /**
   *
   * @return the route that handles rengistration of a user
   */
  def registerRoute: Route = post {
    //allow users to register
    path("register") {
      entity(Directives.as[RegisterModel]) { request =>

        val registerFuture: Future[Boolean] = userManager.register(User(userManager.generateUserId(request.email),
          request.userName,
          request.mobileNumber,
          Seq(),
          request.email,
          request.password))
        onComplete[Boolean](registerFuture) {
          case Success(value) =>
            if (value) {
              complete(StatusCodes.OK.intValue() -> OutputMessage(StatusCodes.OK.intValue(),
                "Registration Successful"))
            } else {
              complete(StatusCodes.CONFLICT.intValue() -> OutputMessage(StatusCodes.CONFLICT.intValue(),
                "Registration Failed, provided email is already registered"))
            }
          case Failure(exception) =>
            logger.error(s"Error occurred at ${new Date().getTime}: Exception reads: ${exception.getMessage}")
            exception match {
              case badEmailEx: BadEmailPatternException =>
                complete(badEmailEx.status() -> OutputMessage(badEmailEx.status(), badEmailEx.getMessage))

              case _ =>
                complete(StatusCodes.INTERNAL_SERVER_ERROR.intValue() -> OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(),
                  "An internal error occurred. Contact the admin."))
            }
        }
      }
    }
  }

  /**
   *
   * @return the route that handles logging in of the user (and login token generation in header)
   */
  def loginRoute: Route = post {
    //allow users to login
    path("login") {
      entity(Directives.as[LoginModel]) { request =>

        onComplete(userManager.login(request.email, request.password)) {
          case Success(value) =>

            respondWithHeaders(RawHeader("Token", value)) {
              complete(StatusCodes.OK.intValue() -> OutputMessage(StatusCodes.OK.intValue(),
              "Login Successful"))
          }
          case Failure(exception) =>
            logger.error(s"Error occurred at ${new Date().getTime}: Exception reads: ${exception.getMessage}")
            exception match {
              case badEmailEx: BadEmailPatternException =>
                complete(badEmailEx.status() -> OutputMessage(badEmailEx.status(), badEmailEx.getMessage))
              case accountNotFoundEx: AccountDoesNotExistException =>
                complete(accountNotFoundEx.status() -> OutputMessage(accountNotFoundEx.status(), accountNotFoundEx.getMessage))
              case incorrectPasswordEx: PasswordMismatchException =>
                complete(incorrectPasswordEx.status() -> OutputMessage(incorrectPasswordEx.status(), incorrectPasswordEx.getMessage))
              case unverifiedEx: UnverifiedAccountException =>
                complete(unverifiedEx.status() -> OutputMessage(unverifiedEx.status(), unverifiedEx.getMessage))
              case _ =>
                complete(StatusCodes.INTERNAL_SERVER_ERROR.intValue() -> OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(),
                  "An internal error occurred. Contact the admin."))
            }
        }
      }
    }
  }

  /**
   *
   * @return the route that allows user to add an address to his/her account
   */
  def addAddressRoute: Route = post {
    path("address") {
      entity(Directives.as[Address]) { request =>
        headerValueByName("Authorization") { token =>

          if (TokenManager.isValidToken(token.split(" ")(1))) {

            val userId = TokenManager.getIdentifier(token.split(" ")(1))
            onComplete(userManager.addAddress(userId, request)) {
              case Success(value) =>
                if (value) {
                  complete(StatusCodes.OK.intValue() -> OutputMessage(StatusCodes.OK.intValue(),
                    "Address added successfully."))
                } else {
                  complete(StatusCodes.INTERNAL_SERVER_ERROR.intValue() -> OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(),
                    "Address could not be added. Please contact the admin."))
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
   *
   * @return the route that shows all addresses that are part of the user's account
   */
  def getAddresses: Route = Directives.get {

    path("address") {
      headerValueByName("Authorization") { token =>
        if (TokenManager.isValidToken(token.split(" ")(1))) {
          val userId = TokenManager.getIdentifier(token.split(" ")(1))

          onComplete(userManager.getAddresses(userId)) {
            case Success(value) =>
              complete(StatusCodes.OK.intValue() -> value)
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

  /**
   *
   * @return route to verify a user account
   */
  def verifyRoute: Route = Directives.get {
    path("verify") {
      parameters("otp", "email"){(otp, email) =>
        val otpData = Otp(otp.toInt, email)
        val otpFuture = userManager.verifyUser(otpData)
        onComplete(otpFuture){
          case Success(value) =>
            if(value){
              complete(
                StatusCodes.OK.intValue() ->
                  OutputMessage(StatusCodes.OK.intValue(), "Email verified successfully")
              )
            }
            else{
              complete(
                StatusCodes.BAD_REQUEST.intValue() ->
                  OutputMessage(StatusCodes.BAD_REQUEST.intValue(), "Otp and email don't match.")
              )
            }

          case Failure(exception) =>

            logger.error(s"Exception occurred at ${new Date().getTime} and Exception reads: ${exception.getMessage}")
            complete(
              StatusCodes.INTERNAL_SERVER_ERROR.intValue() ->
                OutputMessage(StatusCodes.INTERNAL_SERVER_ERROR.intValue(), "Some error occurred, contact the admin.")
            )
        }
      }
    }
  }
}
