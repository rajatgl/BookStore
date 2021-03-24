package com.bridgelabz.bookstoretest.database.mongodb

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection}
import com.bridgelabz.bookstore.models.Otp
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime

import scala.concurrent.Await

class OtpCollectionTest extends AnyFlatSpec {
  val otpCollection: ICrud[Otp] = new DatabaseCollection[Otp]("userOtpTest", CodecRepository.OTP)

  "When created Otp" should "return Future of Completed " in {
    val otp = TestVariables.otp()
    val createTest = Await.result(otpCollection.create(otp), 1500.seconds)
    assert(createTest.toString === "The operation completed successfully")
  }

  "When searched for otp list" should "return Future " in {
    val readTest = Await.result(otpCollection.read(), 1500.seconds)
    assert(readTest.length === 1)
  }

  "When updated otp using email" should "return Future of Updated " in {
    val otp = TestVariables.otp()
    val updateTest = Await.result(otpCollection.update(TestVariables.otp().email, otp, "email"), 1500.seconds)
    assert(updateTest.toString === "AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}")
  }

  "When deleted otp" should "return Future of Deleted " in {
    val deleteTest = Await.result(otpCollection.delete(TestVariables.otp().email, "email"), 1500.seconds)
    assert(deleteTest.toString === "AcknowledgedDeleteResult{deletedCount=1}")
  }

}
