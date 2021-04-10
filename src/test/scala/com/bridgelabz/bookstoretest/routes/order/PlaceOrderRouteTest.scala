package com.bridgelabz.bookstoretest.routes.order

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.database.managers.upgraded.OrderManager
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.routes.OrderRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class PlaceOrderRouteTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {

  val mockOrderManager: OrderManager = mock[OrderManager]
  lazy val route: Route = new OrderRoutes(mockOrderManager).placeOrder

  "This service" should {

    """Route should fail to place order for
      | Post request to /order with
      | AccountDoesNotExistException""".stripMargin in {
      when(mockOrderManager.placeOrder(TestVariables.user().userId,
        TestVariables.order().transactionId,
        0)).thenReturn(Future.failed(new AccountDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "deliveryAddressIndex" : 0,
           |     "transactionId" : "${TestVariables.order().transactionId}"
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/order",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    """Route should fail to place order for
      | Post request to /order with
      | UnverifiedAccountException""".stripMargin in {
      when(mockOrderManager.placeOrder(TestVariables.user().userId,
        TestVariables.order().transactionId,
        0)).thenReturn(Future.failed(new UnverifiedAccountException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "deliveryAddressIndex" : 0,
           |     "transactionId" : "${TestVariables.order().transactionId}"
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/order",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    """Route should fail to place order for
      | Post request to /order with
      | AddressNotFoundException""".stripMargin in {
      when(mockOrderManager.placeOrder(TestVariables.user().userId,
        TestVariables.order().transactionId,
        1)).thenReturn(Future.failed(new AddressNotFoundException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "deliveryAddressIndex" : 1,
           |     "transactionId" : "${TestVariables.order().transactionId}"
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/order",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }

    """Route should fail to place order for
      | Post request to /order with
      | ProductDoesNotExistException""".stripMargin in {
      when(mockOrderManager.placeOrder(TestVariables.user().userId,
        TestVariables.order().transactionId,
        0)).thenReturn(Future.failed(new ProductDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "deliveryAddressIndex" : 0,
           |     "transactionId" : "${TestVariables.order().transactionId}"
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/order",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }

    """Route should fail to place order for
      | Post request to /order with
      | ProductQuantityUnavailableException""".stripMargin in {
      when(mockOrderManager.placeOrder(TestVariables.user().userId,
        TestVariables.order().transactionId,
        0)).thenReturn(Future.failed(new ProductQuantityUnavailableException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "deliveryAddressIndex" : 0,
           |     "transactionId" : "${TestVariables.order().transactionId}"
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/order",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.BAD_REQUEST)
      }
    }

    "Route should add item to the cart for Post request to /cart/item" in {
      when(mockOrderManager.placeOrder(TestVariables.user().userId,
        TestVariables.order().transactionId,
        0)).thenReturn(Future.successful(""))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "deliveryAddressIndex" : 0,
           |     "transactionId" : "${TestVariables.order().transactionId}"
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/order",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.OK)
      }
    }

  }
}
