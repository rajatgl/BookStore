package com.bridgelabz.bookstoretest

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseConfig}
import com.bridgelabz.bookstore.models.{Address, Otp, User}
import org.scalatest.flatspec.AnyFlatSpec
import concurrent.duration._
import scala.concurrent.Await

class DatabaseTest extends AnyFlatSpec {
  val userDatabase: ICrud[User] = new DatabaseConfig[User]("users",CodecRepository.USER)
  val otpDatabase: ICrud[Otp] = new DatabaseConfig[Otp]("userOtp",CodecRepository.OTP)

  "When created user" should "return Future of Completed " in {
    val user = User("12234","Michael Scott","0000000000",Seq(Address("1","Sky","12","Scranton","Bom","123")),"abc@gmail.com","demo123")
    val u = Await.result(userDatabase.create(user),1500.seconds)
    assert(u.toString == "The operation completed successfully")
  }
  "When updated user using email" should "return Future of Updated " in {
    val user = User("12234","Michael Scott","0000000000",Seq(Address("1","Sky","12","Scranton","Bom","123")),"abc@gmail.com","demo123")
    val u = Await.result(userDatabase.update("abc@gmail.com",user,"email"),1500.seconds)
    assert(u.toString == "AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}")
  }
  "When deleted user" should "return Future of Deleted " in {
    val u = Await.result(userDatabase.delete("abc@gmail.com","email"),1500.seconds)
    assert(u.toString == "AcknowledgedDeleteResult{deletedCount=1}")
  }
  "When searched for user" should "return Future " in {
    val u = Await.result(userDatabase.read(),1500.seconds)
    assert(u.length == 10)
  }
  "When created Otp" should "return Future of Completed " in {
    val otp = Otp(1234,"user@gmail.com")
    val u = Await.result(otpDatabase.create(otp),1500.seconds)
    assert(u.toString == "The operation completed successfully")
  }
  "When updated otp using email" should "return Future of Updated " in {
    val otp = Otp(1234,"user@gmail.com")
    val u = Await.result(otpDatabase.update("user@gmail.com",otp,"email"),1500.seconds)
    assert(u.toString == "AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}")
  }
  "When deleted otp" should "return Future of Deleted " in {
    val u = Await.result(otpDatabase.delete("user@gmail.com","email"),1500.seconds)
    assert(u.toString == "AcknowledgedDeleteResult{deletedCount=1}")
  }
  "When searched for otp list" should "return Future " in {
    val u = Await.result(otpDatabase.read(),1500.seconds)
    assert(u.length == 0)
  }
}