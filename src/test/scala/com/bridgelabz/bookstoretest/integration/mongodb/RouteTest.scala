package com.bridgelabz.bookstoretest.integration.mongodb

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseConfig}
import com.bridgelabz.bookstore.models.{Otp, User}
import com.bridgelabz.bookstore.routes.UserRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar


/**
 * PLEASE RUN THE SUITE TEST FUNCTIONS TOGETHER
 */
class RouteTest extends AnyWordSpec
  with ScalatestRouteTest
  with MockitoSugar
  with ScalaFutures  {

  var token: String = "invalid_token"

  val userDatabase: ICrud[User] = new DatabaseConfig[User](
    "userTest",
    CodecRepository.USER
  )

  val otpDatabase: ICrud[Otp] = new DatabaseConfig[Otp](
    "userOtpTest",
    CodecRepository.OTP
  )

  val userManager: UserManager = new UserManager(userDatabase, otpDatabase)
  lazy val routes: UserRoutes = new UserRoutes(userManager)

  "The service" should {

    "Routes should register a test account for a Post request to /register" in {

      val jsonRequest = ByteString(
        s"""
            {
              "userName": "${TestVariables.user().userName}",
              "mobileNumber": "${TestVariables.user().mobileNumber}",
              "email": "${TestVariables.user().email}",
              "password": "${TestVariables.user().password}"
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/register",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> routes.registerRoute ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "Routes should fail to login a test account for a Post request to /login with unverified email" in {

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

      postRequest ~> routes.loginRoute ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    "Utility test to verify the user" in {
      userManager.verifyUser(TestVariables.user().email)
    }

    "Route should verify a user account for Get request to /verify" in {

      Get(s"/verify?otp=${TestVariables.otp().data}&email=${TestVariables.otp().email}") ~> routes.verifyRoute ~>
        check {
          assert(status === StatusCodes.BAD_REQUEST)
        }
    }

    "Routes should login a test account for a Post request to /login" in {

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

      postRequest ~> routes.loginRoute ~> check {
        assert(status === StatusCodes.OK)
        token = header("token").get.value
      }
    }

    "Route should fetch all addresses from a user account for Get request to /address" in {

      Get("/address") ~>
        addCredentials(OAuth2BearerToken(token)) ~>
        routes.getAddresses ~>
        check {
          status.equals(StatusCodes.OK)
        }
    }

    "Route should add address to a user account for Post request to /address" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "apartmentNumber": "${TestVariables.address().apartmentNumber}",
           |    "apartmentName": "${TestVariables.address().apartmentName}",
           |    "streetAddress": "${TestVariables.address().streetAddress}",
           |    "landMark": "${TestVariables.address().landMark}",
           |    "state": "${TestVariables.address().state}",
           |    "pinCode":"${TestVariables.address().pinCode}"
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/address",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> routes.addAddressRoute ~> check {
        assert(status === StatusCodes.OK)
      }
    }
  }
}
