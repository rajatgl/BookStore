package com.bridgelabz.bookstoretest.database.mongodb

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseConfig}
import com.bridgelabz.bookstore.models.Otp
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime

import scala.concurrent.Await

class OtpCollectionTest extends AnyFlatSpec {
  val otpDatabase: ICrud[Otp] = new DatabaseConfig[Otp]("userOtpTest", CodecRepository.OTP)

  "When created Otp" should "return Future of Completed " in {
    val otp = TestVariables.otp()
    val createTest = Await.result(otpDatabase.create(otp), 1500.seconds)
    assert(createTest.toString === "The operation completed successfully")
  }

  "When searched for otp list" should "return Future " in {
    val readTest = Await.result(otpDatabase.read(), 1500.seconds)
    assert(readTest.length === 1)
  }

  "When updated otp using email" should "return Future of Updated " in {
    val otp = TestVariables.otp()
    val updateTest = Await.result(otpDatabase.update(TestVariables.otp().email, otp, "email"), 1500.seconds)
    assert(updateTest.toString === "AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}")
  }

  "When deleted otp" should "return Future of Deleted " in {
    val deleteTest = Await.result(otpDatabase.delete(TestVariables.otp().email, "email"), 1500.seconds)
    assert(deleteTest.toString === "AcknowledgedDeleteResult{deletedCount=1}")
  }
}
