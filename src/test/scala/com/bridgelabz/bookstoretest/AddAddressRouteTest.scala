package com.bridgelabz.bookstoretest

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, UnverifiedAccountException}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.routes.UserRoutes
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class AddAddressRouteTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {
  val mockUserManager: UserManager = mock[UserManager]
  lazy val routes: Route = new UserRoutes(mockUserManager).addAddressRoute

  "This service" should {
    "Route should add address to a user account for Post request to /address" in {
      when(mockUserManager.addAddress(
        TestVariables.user().userId,
        TestVariables.address())).thenReturn(Future.successful(true))

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


      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addHeader("Authorization", s"Bearer $authorization") ~> routes ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "Route should fail to add address to a user account for Post request to /address due to some-internal-error" in {
      when(mockUserManager.addAddress(
        TestVariables.user().userId, TestVariables.address())).thenReturn(Future(false))

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

      postRequest ~> addCredentials(
        OAuth2BearerToken(
          TokenManager.generateToken(TestVariables.user().userId))) ~> routes ~> check {
        assert(status === StatusCodes.INTERNAL_SERVER_ERROR)
      }
    }

    "Route should fail to add address to a user account for Post request to /address due to account-not-found-exception" in {
      when(mockUserManager.addAddress(
        TestVariables.user().userId,TestVariables.address())).thenReturn(
        Future.failed(new AccountDoesNotExistException))

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

      postRequest ~> addCredentials(
        OAuth2BearerToken(
          TokenManager.generateToken(TestVariables.user().userId))) ~> routes ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    "Route should fail to add address to a user account for Post request to /address due to unverified-account-exception" in {
      when(mockUserManager.addAddress(
        TestVariables.user().userId,TestVariables.address())).thenReturn(
        Future.failed(new UnverifiedAccountException))

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

      postRequest ~> addCredentials(
        OAuth2BearerToken(
          TokenManager.generateToken(TestVariables.user().userId))) ~> routes ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    "Route should fail to add address to a user account for Post request to /address due to some-internal-exception" in {
      when(mockUserManager.addAddress(
        TestVariables.user().userId,TestVariables.address())).thenReturn(
        Future.failed(new Exception))

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

      postRequest ~> addCredentials(
        OAuth2BearerToken(
          TokenManager.generateToken(TestVariables.user().userId))) ~> routes ~> check {
        assert(status === StatusCodes.INTERNAL_SERVER_ERROR)
      }
    }

    "Route should fail to add address to a user account for Post request to /address due to invalid token" in {

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

      postRequest ~> addCredentials(
        OAuth2BearerToken("invalid")) ~> routes ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

  }

}
