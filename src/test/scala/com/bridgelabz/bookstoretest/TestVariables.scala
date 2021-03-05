package com.bridgelabz.bookstoretest

import com.bridgelabz.bookstore.models.{Address, User}

/**
 * Created on 3/5/2021.
 * Class: TestVariables.scala
 * Author: Rajat G.L.
 */
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
}
