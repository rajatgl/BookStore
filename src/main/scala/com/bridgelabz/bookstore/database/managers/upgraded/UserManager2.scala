package com.bridgelabz.bookstore.database.managers.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.exceptions.AccountDoesNotExistException
import com.bridgelabz.bookstore.models.{Otp, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserManager2(userCollection: ICrudRepository[User], otpCollection: ICrudRepository[Otp])
  extends UserManager(userCollection,otpCollection) {

  /**
   *
   * @param userId to be checked for existence in database
   *  @return Future of true if email exists in database else false
   */
  override def doesUserExist(userId: String): Future[Boolean] = {
    userCollection.read(userId, "userId").map(seq => {
      seq.nonEmpty
    })
  }

  /**
   *
   * @param email of the account to be fetched from the database
   *  @return a valid user object if account found else None
   */
  override def getUserByEmail(email: String): Future[Option[User]] = {
    userCollection.read(email, "email").map(seq => {
      seq.headOption
    })
  }

  /**
   *
   * @param userId of the account to be fetched from the database
   *  @return a valid user object if account found else None
   */
  override def getUserByUserId(userId: String): Future[Option[User]] = {
    userCollection.read(userId, "userId").map(seq => {
      seq.headOption
    })
  }

  /**
   *
   * @param token to check in the database
   *  @return true if the token was found in the database, false otherwise
   */
  override def doesOtpExist(token: Otp): Future[Boolean] = {
    otpCollection.read(token.data, "data").map(seq => {
      seq.filter(otp => otp.email.equals(token.email))
      seq.nonEmpty
    })
  }
}
