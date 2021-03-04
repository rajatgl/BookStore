package com.bridgelabz.bookstoretest

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.exceptions.BadEmailPattern
import com.bridgelabz.bookstore.models.{Otp, User}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{a, convertToAnyShouldWrapper}
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}

class FunctionTest extends AnyFlatSpec with MockitoSugar{

  val iCrudUserMock: ICrud[User] = mock[ICrud[User]]
  val iCrudOtpMock: ICrud[Otp] = mock[ICrud[Otp]]
  val userManager: UserManager = new UserManager(iCrudUserMock,iCrudOtpMock)

  "Email Regex" should "return false if email has bad pattern" in {
    assert(!userManager.emailRegex("test"))
  }
  "Email Regex" should "return true if email has good pattern" in {
    assert(userManager.emailRegex("test@test.com"))
  }

  "Does Exist" should "return false if email is not registered with database" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User]()))
    assert(!Await.result(userManager.doesExist("test@test.com"),100.millis))
  }

  "Does Exist" should "return true if email is registered with database" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User](User("test@test.com",""))))
    assert(Await.result(userManager.doesExist("test@test.com"),100.millis))
  }

  "Register" should "return failed future in case of bad email pattern" in {
    val registerTest = userManager.register(User("test",""))
    ScalaFutures.whenReady(registerTest.failed){
      e => e shouldBe a [BadEmailPattern]
    }
  }

  "Register" should "return false if user is already registered" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User](User("test@test.com",""))))
    assert(!Await.result(userManager.register(User("test@test.com","")),100.millis))
  }

  "Register" should "return true if user is successfully registered" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User]()))
    when(iCrudUserMock.create(User("test@test.com",""))).thenReturn(Future())
    when(iCrudOtpMock.create(any[Otp])).thenReturn(Future())
    assert(Await.result(userManager.register(User("test@test.com","")),200.millis))
  }
}
