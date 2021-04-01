package com.bridgelabz.bookstoretest.routes.cart

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.database.managers.upgraded.CartManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, CartDoesNotExistException, ProductDoesNotExistException, UnverifiedAccountException}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.routes.CartRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class RemoveItemRouteTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {

  val mockCartManager: CartManager = mock[CartManager]
  lazy val route: Route = new CartRoutes(mockCartManager).removeItem

  "This service " should {

    "Route should remove an item from the cart for Delete request to /cart/remove" in {
      when(mockCartManager.removeItem(
        TestVariables.user().userId,
        TestVariables.cart().items.head.productId)).thenReturn(Future.successful(true))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/cart/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    """Route should fail to remove an item from the cart for
      | Delete request to /cart/remove
      |  with AccountDoesNotExistException""".stripMargin in {

      when(mockCartManager.removeItem(
        TestVariables.user().userId,
        TestVariables.cart().items.head.productId
      )).thenReturn(Future.failed(new AccountDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/cart/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }

    }

    """Route should fail to remove an item from the cart for
      | Delete request to /cart/remove
      |  with UnverifiedAccountException""".stripMargin in {

      when(mockCartManager.removeItem(
        TestVariables.user().userId,
        TestVariables.cart().items.head.productId
      )).thenReturn(Future.failed(new UnverifiedAccountException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/cart/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }

    }

    """Route should fail to remove an item from the cart for
      | Delete request to /cart/remove
      |  with ProductDoesNotExistException""".stripMargin in {

      when(mockCartManager.removeItem(
        TestVariables.user().userId,
        TestVariables.cart().items.head.productId
      )).thenReturn(Future.failed(new ProductDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/cart/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }

    }

    """Route should fail to remove an item from the cart for
      | Delete request to /cart/remove
      |  with CartDoesNotExistException""".stripMargin in {

      when(mockCartManager.removeItem(
        TestVariables.user().userId,
        TestVariables.cart().items.head.productId
      )).thenReturn(Future.failed(new CartDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/cart/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }

    }





  }
}
