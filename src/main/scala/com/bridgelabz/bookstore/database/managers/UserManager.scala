package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.exceptions.BadEmailPattern
import com.bridgelabz.bookstore.models.{Otp, User}
import com.bridgelabz.bookstore.utils.Utilities

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserManager(database: ICrud[User], otpDatabase: ICrud[Otp]) {

  def emailRegex(email: String): Boolean = email.matches(System.getenv("EMAIL_REGEX"))

  def doesExist(email: String): Future[Boolean] =
    database.read().map(users => {
      var isExist = false
      for (user <- users) {
        if (email.equals(user.email)) {
          isExist = true
        }
      }
      isExist
    })

  def register(user: User): Future[Boolean] = {
    if(emailRegex(user.email)){

      val existsFuture = doesExist(user.email)
      existsFuture.map(exists => {
        if(exists){
          false
        }
        else{
          database.create(user)

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
