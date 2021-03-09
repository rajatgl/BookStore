package com.bridgelabz.bookstoretest

import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.Main
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.exceptions.BadEmailPatternException
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class RegisterRouteTest extends AnyWordSpec with ScalatestRouteTest with MockitoSugar with ScalaFutures {
  val mockUserManager: UserManager = mock[UserManager]
  "The service" should {
    "Routes should register a test account for a Post request to /register" in {
      when(mockUserManager.register(
        TestVariables.user(userId = mockUserManager.generateUserId(
          TestVariables.user().email))
      )).thenReturn(Future(true))

      val jsonRequest = ByteString(
        s"""
            {
              "userName": ${TestVariables.user().userName},
              "mobileNumber": ${TestVariables.user().mobileNumber},
              "email": ${TestVariables.user().email},
              "password": ${TestVariables.user().password}
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/register",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> Main.route(mockUserManager) ~> check {
        status.equals(StatusCodes.OK)
      }
    }

    "Routes should fail to register a test account for a Post request to /register if account already exist" in {
      when(mockUserManager.register(
        TestVariables.user(userId = mockUserManager.generateUserId(
          TestVariables.user().email))
      )).thenReturn(Future(false))

      val jsonRequest = ByteString(
        s"""
            {
              "userName": ${TestVariables.user().userName},
              "mobileNumber": ${TestVariables.user().mobileNumber},
              "email": ${TestVariables.user().email},
              "password": ${TestVariables.user().password}
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/register",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> Main.route(mockUserManager) ~> check {
        status.equals(StatusCodes.Conflict)
      }
    }

    "Routes should throw bad-email-pattern-exception for a Post request to /register if bad email is found" in {
      when(mockUserManager.register(
        TestVariables.user(
          userId = mockUserManager.generateUserId(TestVariables.user().email)
        ))).thenReturn(Future(throw new BadEmailPatternException))

      val jsonRequest = ByteString(
        s"""
            {
              "userName": ${TestVariables.user().userName},
              "mobileNumber": ${TestVariables.user().mobileNumber},
              "email": ${TestVariables.user().email},
              "password": ${TestVariables.user().password}
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/register",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> Main.route(mockUserManager) ~> check {
        status.equals(StatusCodes.BadRequest)
      }
    }

    "Routes should throw internal-server-error-exception for a Post request to /register if some exception occurred" in {
      when(mockUserManager.register(
        TestVariables.user(
          userId = mockUserManager.generateUserId(TestVariables.user().email)
        ))).thenReturn(Future(throw new Exception))

      val jsonRequest = ByteString(
        s"""
            {
              "userName": ${TestVariables.user().userName},
              "mobileNumber": ${TestVariables.user().mobileNumber},
              "email": ${TestVariables.user().email},
              "password": ${TestVariables.user().password}
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/register",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> Main.route(mockUserManager) ~> check {
        status.equals(StatusCodes.InternalServerError)
      }
    }

  }
}