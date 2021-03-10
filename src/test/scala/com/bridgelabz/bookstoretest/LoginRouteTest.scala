package com.bridgelabz.bookstoretest

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, BadEmailPatternException, PasswordMismatchException, UnverifiedAccountException}
import com.bridgelabz.bookstore.routes.UserRoutes
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class LoginRouteTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {
  val mockUserManager: UserManager = mock[UserManager]
  lazy val routes: Route = new UserRoutes(mockUserManager).loginRoute

  "The service" should {
    "Routes should login a test account for a Post request to /login" in {
      when(mockUserManager.login(
        TestVariables.user().email, TestVariables.user().password)).thenReturn(
        Future.successful("TOKEN"))

      val jsonRequest = ByteString(
        s"""
            {
              "email": "${TestVariables.user().email}",
              "password": "${TestVariables.user().password}"
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> routes ~> check {
        assert(status === StatusCodes.OK)
        assert(header("Token").isDefined && header("Token").get.value === "TOKEN")
      }
    }


    "Routes should fail to login a test account for a Post request to /login with bad-email-pattern" in {
      when(mockUserManager.login(
        "test", TestVariables.user().password)).thenReturn(
        Future.failed(new BadEmailPatternException))

      val jsonRequest = ByteString(
        s"""
            {
              "email": "test",
              "password": "${TestVariables.user().password}"
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> routes ~> check {
        assert(status === StatusCodes.BadRequest)
      }
    }

    "Routes should fail to login a test account for a Post request to /login with unregistered email" in {
      when(mockUserManager.login(
        TestVariables.user().email, TestVariables.user().password)).thenReturn(
        Future.failed(new AccountDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
            {
              "email": "${TestVariables.user().email}",
              "password": "${TestVariables.user().password}"
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> routes ~> check {
        assert(status === StatusCodes.Unauthorized)
      }
    }

    "Routes should fail to login a test account for a Post request to /login with incorrect password" in {
      when(mockUserManager.login(
        TestVariables.user().email, TestVariables.user().password)).thenReturn(
        Future.failed(new PasswordMismatchException))

      val jsonRequest = ByteString(
        s"""
            {
              "email": "${TestVariables.user().email}",
              "password": "${TestVariables.user().password}"
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> routes ~> check {
        assert(status === StatusCodes.Unauthorized)
      }
    }


    "Routes should fail to login a test account for a Post request to /login with unverified email" in {
      when(mockUserManager.login(
        TestVariables.user().email, TestVariables.user().password)).thenReturn(
        Future.failed(new UnverifiedAccountException))

      val jsonRequest = ByteString(
        s"""
            {
              "email": "${TestVariables.user().email}",
              "password": "${TestVariables.user().password}"
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> routes ~> check {
        assert(status === StatusCodes.Unauthorized)
      }
    }

    "Routes should fail to login a test account for a Post request to /login because of some internal exception" in {
      when(mockUserManager.login(
        TestVariables.user().email, TestVariables.user().password)).thenReturn(
        Future.failed(new Exception))

      val jsonRequest = ByteString(
        s"""
            {
              "email": "${TestVariables.user().email}",
              "password": "${TestVariables.user().password}"
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> routes ~> check {
        assert(status===StatusCodes.InternalServerError)
      }
    }

  }
}