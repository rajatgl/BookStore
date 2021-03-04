package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.exceptions.BadEmailPattern
import com.bridgelabz.bookstore.models.{Otp, User}
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
   * @param email to be checked for existence in database
   * @return Future of true if email exists in database else false
   */
  def doesExist(email: String): Future[Boolean] =
    userDatabase.read().map(users => {
      var isExist = false
      for (user <- users) {
        if (email.equals(user.email)) {
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
    if(emailRegex(user.email)){

      val existsFuture = doesExist(user.email)
      existsFuture.map(exists => {
        if(exists){
          false
        }
        else{
          userDatabase.create(user)

          val newOtp = Otp(Utilities.randomNumber(), user.email)
          otpDatabase.create(newOtp)
          EmailManager.sendOtp(newOtp)
          true
        }
      })
    }
    else{
      Future.failed[Boolean](new BadEmailPattern)
    }
  }
}
