package com.bridgelabz.bookstoretest

import com.bridgelabz.bookstore.models.{Address, Otp, Product, User}

/**
 * Created on 3/5/2021.
 * Class: TestVariables.scala
 * Author: Rajat G.L.
 */

// scalastyle:off
object TestVariables {
  def user(userId: String = "test502",
           userName: String = "Test",
           mobileNumber: String = "1234567891",
           addresses: Seq[Address] = Seq(),
           email: String = "test@test.com",
           password: String = "",
           verificationComplete: Boolean = false): User =

    User(userId, userName, mobileNumber, addresses, email, password, verificationComplete)

  def address(apartmentNumber: String = "1",
              apartmentName: String = "Hello",
              streetAddress: String = "World",
              landMark: String = "Goodbye",
              state: String = "Universe",
              pinCode: String = "000000"): Address =

    Address(apartmentNumber, apartmentName, streetAddress, landMark, state, pinCode)

  def otp(data: Int = 123,
          email: String = "test@test.com"): Otp =

    Otp(data,email)

  def product(productId: String = "1",
              author: String = "Xrnes",
              title: String = "HiBook",
              image: String = "12323434",
              quantity: String = "2",
              price: String = "3000",
              description: String = "This is a test product"): Product =

    Product(productId,author,title,image,quantity,price,description)
}
