package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, BadEmailPatternException}
import com.bridgelabz.bookstore.models.{Address, Otp, User}
import com.bridgelabz.bookstore.utils.Utilities

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserManager(userDatabase: ICrud[User], otpDatabase: ICrud[Otp]) {
  /**
   *
   * @param email whose pattern is to be verified
   * @return true if email matches the pattern else false
   */
  def emailRegex(email: String): Boolean = email.matches(System.getenv("EMAIL_REGEX"))

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
   * @param userId of the user whose address is to be updated
   * @param address passed by the user
   * @return future of true if successfully updated else false
   */
  def addAddress(userId: String, address: Address): Future[Boolean] = {
    userDatabase.read().map(users => {
      var didUpdate = false
      for (user <- users) {
        if (user.userId.equals(userId)) {
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
        }
      }

      if(!didUpdate){
        throw new AccountDoesNotExistException
      }
      else{
        didUpdate
      }
    })
  }

  /**
   *
   * @param userId of the user whose address is to be fetched
   * @return addresses associated with the user
   */
  def getAddresses(userId: String):Future[Seq[Address]] ={
    userDatabase.read().map(users => {
      var addresses: Seq[Address] = Seq()
      var doesExist = false
      for (user <- users) {
        if (user.userId.equals(userId)) {
          addresses = user.addresses
          doesExist = true
        }
      }
      if(doesExist) {
        addresses
      }
      else{
        throw new AccountDoesNotExistException
      }
    })
  }

}
