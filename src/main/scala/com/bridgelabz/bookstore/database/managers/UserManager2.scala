package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.models.{Otp, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserManager2(userDatabase: ICrudRepository[User], otpDatabase: ICrudRepository[Otp])
  extends UserManager(userDatabase,otpDatabase) {

  override def doesUserExist(userId: String): Future[Boolean] = {
    userDatabase.readByValue(userId, "userId").map(seq => {
      seq.nonEmpty
    })
  }

  override def getUserByEmail(email: String): Future[Option[User]] = {
    userDatabase.readByValue(email, "email").map(seq => {
      seq.headOption
    })
  }

  override def getUserByUserId(userId: String): Future[Option[User]] = {
    userDatabase.readByValue(userId, "userId").map(seq => {
      seq.headOption
    })
  }

  override def doesOtpExist(token: Otp): Future[Boolean] = {
    otpDatabase.readByValue(token.data, "data").map(seq => {
      seq.filter(otp => otp.email.equals(token.email))
      seq.nonEmpty
    })
  }
}
