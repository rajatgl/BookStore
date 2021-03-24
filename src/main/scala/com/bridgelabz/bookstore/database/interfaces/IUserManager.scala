package com.bridgelabz.bookstore.database.interfaces

import com.bridgelabz.bookstore.models.{Address, Otp, User}

import scala.concurrent.Future

trait IUserManager {

  def register(user: User): Future[Boolean]

  def verifyUser(token: Otp): Future[Boolean]

  def login(email: String, password: String): Future[String]

  def addAddress(userId: String, address: Address): Future[Boolean]

  def getAddresses(userId: String): Future[Seq[Address]]

  def generateUserId(email: String): String
}
