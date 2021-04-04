package com.bridgelabz.bookstore.database.managers.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.exceptions.AccountDoesNotExistException
import com.bridgelabz.bookstore.models.{Otp, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserManager2(userCollection: ICrudRepository[User], otpCollection: ICrudRepository[Otp])
  extends UserManager(userCollection,otpCollection) {

  override def doesUserExist(userId: String): Future[Boolean] = {
    userCollection.read(userId, "userId").map(seq => {
      seq.nonEmpty
    })
  }

  override def getUserByEmail(email: String): Future[Option[User]] = {
    userCollection.read(email, "email").map(seq => {
      seq.headOption
    })
  }

  override def getUserByUserId(userId: String): Future[Option[User]] = {
    userCollection.read(userId, "userId").map(seq => {
      seq.headOption
    })
  }

  override def doesOtpExist(token: Otp): Future[Boolean] = {
    otpCollection.read(token.data, "data").map(seq => {
      seq.filter(otp => otp.email.equals(token.email))
      seq.nonEmpty
    })
  }

  /**
   *
   * @param email of the user to be verified
   * @return future of true if user verified else future fails
   */
  override def verifyUserEmail(email: String): Future[Boolean] = {
//    getUserByEmail(email).map(user => {
//      if(user.isDefined) {
//        userCollection.update(user.get.userId, true, "userId", "verificationComplete")
//        true
//      }
//      else{
//        throw new AccountDoesNotExistException
//      }
//    })
    super.verifyUserEmail(email)
  }
}
