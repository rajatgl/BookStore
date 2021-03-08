package com.bridgelabz.bookstore.database.managers

import java.util.Date

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, BadEmailPatternException, PasswordMismatchException, UnverifiedAccountException}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.managers.EmailManager
import com.bridgelabz.bookstore.models.{Address, Otp, User}
import com.bridgelabz.bookstore.utils.Utilities
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserManager(userDatabase: ICrud[User], otpDatabase: ICrud[Otp]) {

  val logger: Logger = Logger("User-Manager")
  /**
   *
   * @param email whose pattern is to be verified
   * @return true if email matches the pattern else false
   */
  def emailRegex(email: String): Boolean = email.matches(System.getenv("EMAIL_REGEX"))

  /**
   *
   * @param email used to generate userId
   * @return userId
   */

  //TODO: Implement a proper encryption based/ database based user ID generation
  def generateUserId(email: String): String = email.reverse

  /**
   *
   * @param userId to be checked for existence in database
   * @return Future of true if email exists in database else false
   */
  def doesExist(userId: String): Future[Boolean] =
    userDatabase.read().map(users => {
      var isExist = false
      for (user <- users) {
        if (userId.equals(user.userId)) {
          isExist = true
        }
      }
      isExist
    })

  /**
   *
   * @param user to be registered in the database
   * @return Future of true if user gets successfully registered else false
   */
  def register(user: User): Future[Boolean] = {
    if (emailRegex(user.email)) {

      val existsFuture = doesExist(user.userId)
      existsFuture.map(exists => {
        if (exists) {
          false
        }
        else {
          userDatabase.create(user)

          val newOtp = Otp(Utilities.randomNumber(), user.email)
          otpDatabase.create(newOtp)
          EmailManager.sendOtp(newOtp)
          logger.info(s"Otp sent as email at ${new Date().getTime}")
          true
        }
      })
    }
    else {
      Future.failed[Boolean](new BadEmailPatternException)
    }
  }

  /**
   *
   * @param userId  of the user whose address is to be updated
   * @param address passed by the user
   * @return future of true if successfully updated else false
   */
  def addAddress(userId: String, address: Address): Future[Boolean] = {
    userDatabase.read().map(users => {
      var didUpdate = false
      for (user <- users) {
        if (user.userId.equals(userId)) {
          if(user.verificationComplete) {
            val newUser: User = User(
              userId,
              user.userName,
              user.mobileNumber,
              user.addresses :+ address,
              user.email,
              user.password,
              user.verificationComplete
            )
            userDatabase.update(userId, newUser, "userId")
            didUpdate = true
            logger.info(s"Address updated at ${new Date().getTime}")
          }
          else{
            throw new UnverifiedAccountException
          }
        }
      }

      if (!didUpdate) {
        throw new AccountDoesNotExistException
      }
      else {
        didUpdate
      }
    })
  }

  /**
   *
   * @param userId of the user whose address is to be fetched
   * @return addresses associated with the user
   */
  def getAddresses(userId: String): Future[Seq[Address]] = {
    userDatabase.read().map(users => {
      var addresses: Seq[Address] = Seq()
      var doesExist = false
      for (user <- users) {
        if (user.userId.equals(userId)) {
          if(user.verificationComplete) {
            addresses = user.addresses
            doesExist = true
            logger.info(s"Address successfully fetched at ${new Date().getTime}")
          }
          else{
            throw new UnverifiedAccountException
          }
        }
      }
      if (doesExist) {
        addresses
      }
      else {
        throw new AccountDoesNotExistException
      }
    })
  }

  /**
   *
   * @param email of the account to be fetched from the database
   * @return a valid user object if account found else None
   */
  def getUser(email: String): Future[Option[User]] = {
    userDatabase.read().map(users => {
      var isExist = false
      var searchedUser: Option[User] = None
      for (user <- users) {
        if (email.equals(user.email)) {
          isExist = true
          searchedUser = Some(user)
        }
      }
      searchedUser
    })
  }

  /**
   *
   * @param email    belonging to an account
   * @param password of the corresponding email
   * @return JWT token if successfully logged in else throw an exception (failed future)
   */
  def login(email: String, password: String): Future[String] = {
    if(emailRegex(email)){
    getUser(email).map(user =>
      if (user.isDefined) {
        if (user.get.password == password) {
          if(user.get.verificationComplete) {
            logger.info(s"New login token generated at ${new Date().getTime}")
            TokenManager.generateToken(user.get.userId)
          }
          else{
            throw new UnverifiedAccountException
          }
        }
        else {
          throw new PasswordMismatchException
        }
      }
      else {
        throw new AccountDoesNotExistException
      }
    )}
    else
      Future.failed(new BadEmailPatternException)
  }

}
