package com.bridgelabz.bookstoretest

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, BadEmailPatternException, PasswordMismatchException, UnverifiedAccountException}
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
    assert(!Await.result(userManager.doesUserExist("test@test.com"),100.millis))
  }

  "Does Exist" should "return true if email is registered with database" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User](TestVariables.user())))
    assert(Await.result(userManager.doesUserExist("test502"),100.millis))
  }

  "Register" should "return failed future in case of bad email pattern" in {
    val registerTest = userManager.register(TestVariables.user(email = "badEmail"))
    ScalaFutures.whenReady(registerTest.failed){
      e => e shouldBe a[BadEmailPatternException]
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
    assert(Await.result(userManager.register(TestVariables.user()),500.millis))
  }

  "Add address" should "throw user-does-not-exist exception in case user does not exist in database" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User]()))

    val addAddressTest = userManager.addAddress("random", TestVariables.address())
    ScalaFutures.whenReady(addAddressTest.failed){
      e => e shouldBe a[AccountDoesNotExistException]
    }
  }

  "Add address" should "throw unverified-user exception in case user is not verified" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq(TestVariables.user())))

    val addAddressTest = userManager.addAddress(
      TestVariables.user().userId, TestVariables.address())
    ScalaFutures.whenReady(addAddressTest.failed){
      e => e shouldBe a[UnverifiedAccountException]
    }
  }

  "Add address" should "return true if user exists and addresses updated" in {
    when(iCrudUserMock.read()).thenReturn(
      Future[Seq[User]](Seq[User](TestVariables.user(verificationComplete = true))))
    when(iCrudUserMock.update(
      TestVariables.user().userId,
      TestVariables.user(addresses = Seq(TestVariables.address())),
      "userId"
    )).thenReturn(Future())

    val addAddressTest = userManager.addAddress(TestVariables.user().userId, TestVariables.address())
    assert(Await.result(addAddressTest, 200.millis))
  }

  "Get addresses" should "throw user-does-not-exist exception in case user does not exist in database" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User]()))

    val addAddressTest = userManager.getAddresses("random")
    ScalaFutures.whenReady(addAddressTest.failed){
      e => e shouldBe a[AccountDoesNotExistException]
    }
  }

  "Get addresses" should "throw unverified-user exception in case user is not verified" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq(TestVariables.user())))

    val getAddressTest = userManager.getAddresses(TestVariables.user().userId)
    ScalaFutures.whenReady(getAddressTest.failed){
      e => e shouldBe a[UnverifiedAccountException]
    }
  }

  "Get addresses" should "return an sequence of addresses if user exists and addresses are fetched" in {
    when(iCrudUserMock.read()).thenReturn(
      Future[Seq[User]](Seq[User](TestVariables.user(verificationComplete = true))))

    val addAddressTest = userManager.getAddresses(TestVariables.user().userId)
    assert(Await.result(addAddressTest, 200.millis).isInstanceOf[Seq[Address]])
  }

  "login" should "throw bad-email-pattern exception if email has a bad-pattern" in {
    val loginTest: Future[String] = userManager.login("test", "test")
    ScalaFutures.whenReady(loginTest.failed){
      e => e shouldBe a[BadEmailPatternException]
    }
  }

  "login" should "throw account-does-not-exist exception if email does not exist in the database" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User]()))

    val loginTest: Future[String] = userManager.login(
      TestVariables.user().email, TestVariables.user().password)
    ScalaFutures.whenReady(loginTest.failed){
      e => e shouldBe a[AccountDoesNotExistException]
    }
  }

  "login" should "throw password-mismatch exception if password does not match in the database" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq[User](TestVariables.user(password = "something_else"))))

    val loginTest: Future[String] = userManager.login(
      TestVariables.user().email, TestVariables.user().password)
    ScalaFutures.whenReady(loginTest.failed){
      e => e shouldBe a[PasswordMismatchException]
    }
  }

  "login" should "throw unverified-account exception if email is not verified" in {
    when(iCrudUserMock.read()).thenReturn(Future[Seq[User]](Seq(TestVariables.user())))

    val loginTest: Future[String] = userManager.login(
      TestVariables.user().email, TestVariables.user().password)
    ScalaFutures.whenReady(loginTest.failed){
      e => e shouldBe a[UnverifiedAccountException]
    }
  }

  "login" should "return a token if login is successful" in {
    when(iCrudUserMock.read()).thenReturn(
      Future[Seq[User]](Seq(TestVariables.user(verificationComplete = true))))

    val loginTest: Future[String] = userManager.login(
      TestVariables.user().email, TestVariables.user().password)
    assert(Await.result(loginTest,1500.millis).isInstanceOf[String])
  }

  "verifyOtp" should "return future of false if matching Otp is not found in the database" in {
    when(iCrudOtpMock.read()).thenReturn(
      Future[Seq[Otp]](Seq()))

    assert(!Await.result(userManager.doesOtpExist(TestVariables.otp()),200.millis))
  }

  "verifyOtp" should "return future of true if matching Otp is found in the database" in {
    when(iCrudOtpMock.read()).thenReturn(
      Future[Seq[Otp]](Seq(TestVariables.otp())))
    when(iCrudOtpMock.delete(TestVariables.otp().email,"email")).thenReturn(
      Future())
    when(iCrudUserMock.read()).thenReturn(Future(Seq(TestVariables.user())))
    when(iCrudUserMock.update(TestVariables.user().email,TestVariables.user(verificationComplete = true),"email")).thenReturn(
      Future[Seq[Otp]](Seq(TestVariables.otp())))

    assert(Await.result(userManager.doesOtpExist(TestVariables.otp()),200.millis))
  }

  "verifyUser" should "throw account-does-not-exist exception when user to be verified is not in database" in {
    when(iCrudUserMock.read()).thenReturn(Future(Seq()))

    val verifyTest = userManager.verifyUserEmail(TestVariables.user().email)
    ScalaFutures.whenReady(verifyTest.failed){
      e => e shouldBe a[AccountDoesNotExistException]
    }
  }

  "generateUserId" should "generate valid userId for a supplied string" in {
    val verifyTest = userManager.generateUserId("Test")
    assert(verifyTest === "tseT")
  }
}
