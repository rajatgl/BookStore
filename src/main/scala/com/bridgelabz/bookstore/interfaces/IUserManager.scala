package com.bridgelabz.bookstore.interfaces

import com.bridgelabz.bookstore.models.{Address, Otp, User}

import scala.concurrent.Future

trait IUserManager {

  def register(user: User): Future[Boolean]

  def login(email: String, password: String): Future[String]

  def verifyUser(token: Otp): Future[Boolean]

  def addAddress(userId: String, address: Address): Future[Boolean]

  def getAddresses(userId: String): Future[Seq[Address]]

  def generateUserId(email: String): String
}
