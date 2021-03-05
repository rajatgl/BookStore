package com.bridgelabz.bookstoretest

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, BadEmailPatternException}
import com.bridgelabz.bookstore.models.{Address, Otp, User}
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
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User](TestVariables.user())))
    assert(Await.result(userManager.doesExist("test502"),100.millis))
  }

  "Register" should "return failed future in case of bad email pattern" in {
    val registerTest = userManager.register(TestVariables.user(email = "badEmail"))
    ScalaFutures.whenReady(registerTest.failed){
      e => e shouldBe a [BadEmailPatternException]
    }
  }

  "Register" should "return false if user is already registered" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User](TestVariables.user())))
    assert(!Await.result(userManager.register(TestVariables.user()),100.millis))
  }

  "Register" should "return true if user is successfully registered" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User]()))
    when(iCrudUserMock.create(TestVariables.user())).thenReturn(Future())
    when(iCrudOtpMock.create(any[Otp])).thenReturn(Future())
    assert(Await.result(userManager.register(TestVariables.user()),200.millis))
  }

  "Add address" should "should-throw-user-does-not-exist exception in case user does not exist in database" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User]()))

    val addAddressTest = userManager.addAddress("random", TestVariables.address())
    ScalaFutures.whenReady(addAddressTest.failed){
      e => e shouldBe a [AccountDoesNotExistException]
    }
  }

  "Add address" should "should return true if user exists and addresses updated" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User](TestVariables.user())))
    when(iCrudUserMock.update(
      TestVariables.user().userId,
      TestVariables.user(addresses = Seq(TestVariables.address())),
      "userId"
    )).thenReturn(Future())

    val addAddressTest = userManager.addAddress(TestVariables.user().userId, TestVariables.address())
    assert(Await.result(addAddressTest, 200.millis))
  }

  "Get addresses" should "should-throw-user-does-not-exist exception in case user does not exist in database" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User]()))

    val addAddressTest = userManager.getAddresses("random")
    ScalaFutures.whenReady(addAddressTest.failed){
      e => e shouldBe a [AccountDoesNotExistException]
    }
  }

  "Get addresses" should "should return an sequence of addresses if user exists and addresses are fetched" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User](TestVariables.user())))

    val addAddressTest = userManager.getAddresses(TestVariables.user().userId)
    assert(Await.result(addAddressTest, 200.millis).isInstanceOf[Seq[Address]])
  }
}
