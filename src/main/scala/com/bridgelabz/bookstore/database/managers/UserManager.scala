package com.bridgelabz.bookstore.database.managers

import java.util.Date

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, BadEmailPatternException, PasswordMismatchException, UnverifiedAccountException}
import com.bridgelabz.bookstore.interfaces.IUserManager
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.managers.EmailManager
import com.bridgelabz.bookstore.models.{Address, Otp, User}
import com.bridgelabz.bookstore.utils.Utilities
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserManager(userCollection: ICrud[User], otpCollection: ICrud[Otp])
  extends IUserManager {

  val logger: Logger = Logger("User-Manager")

  /**
   *
   * @param user to be registered in the database
   * @return Future of true if user gets successfully registered else false
   */
  def register(user: User): Future[Boolean] = {
    if (emailRegex(user.email)) {

      val existsFuture = doesUserExist(user.userId)
      existsFuture.map(exists => {
        if (exists) {
          false
        }
        else {
          userCollection.create(user)
          val newOtp = Otp(Utilities.randomNumber(), user.email)
          otpCollection.create(newOtp)
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
   * @param email    belonging to an account
   * @param password of the corresponding email
   * @return JWT token if successfully logged in else throw an exception (failed future)
   */
  def login(email: String, password: String): Future[String] = {
    if (emailRegex(email)) {
      getUserByEmail(email).map(user =>
        if (user.isDefined) {
          if (user.get.password == password) {
            if (user.get.verificationComplete) {
              logger.info(s"New login token generated at ${new Date().getTime}")
              TokenManager.generateToken(user.get.userId)
            }
            else {
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
      )
    }
    else {
      Future.failed(new BadEmailPatternException)
    }
  }

  /**
   *
   * @param token to be verified
   * @return future of true if otp verified else false
   */
  def verifyUser(token: Otp): Future[Boolean] = {
    doesOtpExist(token).map(otpExists => {
      if (otpExists) {
        verifyUserEmail(token.email)
        otpCollection.delete(token.email, "email")
        logger.info(s"Otp: ${token.data} used at ${new Date().getTime}")
        true
      }
      else{
        false
      }
    })
  }

  /**
   *
   * @param userId  of the user whose address is to be updated
   * @param address passed by the user
   * @return future of true if successfully updated else false
   */
  def addAddress(userId: String, address: Address): Future[Boolean] = {
    getUserByUserId(userId).map(user => {
      var didUpdate = false
      if (user.isDefined) {
        if (user.get.verificationComplete) {

          val newUser: User = User(
            userId,
            user.get.userName,
            user.get.mobileNumber,
            user.get.addresses :+ address,
            user.get.email,
            user.get.password,
            user.get.verificationComplete
          )
          updateAddresses(newUser)
          didUpdate = true
          logger.info(s"Address updated at ${new Date().getTime}")
        }
        else {
          throw new UnverifiedAccountException
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
    getUserByUserId(userId).map(user => {
      var doesAccountExist = false
      var addresses: Seq[Address] = Seq()
      if (user.isDefined) {
        if (user.get.verificationComplete) {
          addresses = user.get.addresses
          doesAccountExist = true
          logger.info(s"Address successfully fetched at ${new Date().getTime}")
        }
        else {
          throw new UnverifiedAccountException
        }
      }
      if (doesAccountExist) {
        addresses
      }
      else {
        throw new AccountDoesNotExistException
      }
    })
  }

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
  def doesUserExist(userId: String): Future[Boolean] =
    userCollection.read().map(users => {
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
   * @param email of the account to be fetched from the database
   * @return a valid user object if account found else None
   */
  def getUserByEmail(email: String): Future[Option[User]] = {
    userCollection.read().map(users => {
      var searchedUser: Option[User] = None
      for (user <- users) {
        if (email.equals(user.email)) {
          searchedUser = Some(user)
        }
      }
      searchedUser
    })
  }

  /**
   *
   * @param userId of the account to be fetched from the database
   * @return a valid user object if account found else None
   */
  def getUserByUserId(userId: String): Future[Option[User]] = {
    userCollection.read().map(users => {
      var searchedUser: Option[User] = None
      for (user <- users) {
        if (userId.equals(user.userId)) {
          searchedUser = Some(user)
        }
      }
      searchedUser
    })
  }

  /**
   *
   * @param token to check in the database
   * @return true if the token was found in the database, false otherwise
   */
  def doesOtpExist(token: Otp): Future[Boolean] = {
    otpCollection.read().map(otps => {
      otps.filter(otp => otp.email.equals(token.email) && otp.data.equals(token.data))
      otps.nonEmpty
    })
  }

  /**
   *
   * @param email of the user to be verified
   * @return future of true if user verified else future fails
   */
  def verifyUserEmail(email: String): Future[Boolean] = {
    getUserByEmail(email).map(user => {
      if (user.isDefined) {
        val newUser = User(
          user.get.userId,
          user.get.userName,
          user.get.mobileNumber,
          user.get.addresses,
          user.get.email,
          user.get.password,
          verificationComplete = true
        )
        userCollection.update(email, newUser, "email")
        true
      }
      else {
        throw new AccountDoesNotExistException
      }
    })
  }

  /**
   *
   * @param user the new entity which will replace the one in database
   */
  def updateAddresses(user: User): Unit = {
    userCollection.update(user.userId, user, "userId")
  }
}
