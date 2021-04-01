package com.bridgelabz.bookstoretest.routes.cart

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.database.managers.upgraded.CartManager
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.models.CartItem
import com.bridgelabz.bookstore.routes.CartRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class AddItemRouteTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {

  val mockCartManager: CartManager = mock[CartManager]
  lazy val route: Route = new CartRoutes(mockCartManager).addItem

  "This service" should {

    "Route should add item to the cart for Post request to /cart/item" in {
      when(mockCartManager.addItem(
        org.mockito.ArgumentMatchers.eq(TestVariables.user().userId),
        any[CartItem])).thenReturn(Future.successful(true))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId},
           |    "quantity": ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/cart/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    """Route should fail to add item to cart for
      | Post request to /wishlist/item with
      |  AccountDoesNotExistException""".stripMargin in {
      when(mockCartManager.addItem(
        org.mockito.ArgumentMatchers.eq(TestVariables.user().userId),
        any[CartItem])).thenReturn(Future.failed(new AccountDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId},
           |    "quantity": ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/cart/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    """Route should fail to add item to cart for
      | Post request to /wishlist/item with
      |  UnverifiedAccountException""".stripMargin in {
      when(mockCartManager.addItem(
        org.mockito.ArgumentMatchers.eq(TestVariables.user().userId),
        any[CartItem])).thenReturn(Future.failed(new UnverifiedAccountException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId},
           |    "quantity": ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/cart/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    """Route should fail to add item to cart for
      | Post request to /wishlist/item with
      |  ProductDoesNotExistException""".stripMargin in {
      when(mockCartManager.addItem(
        org.mockito.ArgumentMatchers.eq(TestVariables.user().userId),
        any[CartItem])).thenReturn(Future.failed(new ProductDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId},
           |    "quantity": ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/cart/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }

    """Route should fail to add item to cart for
      | Post request to /wishlist/item with
      |  ProductQuantityUnavailableException""".stripMargin in {
      when(mockCartManager.addItem(
        org.mockito.ArgumentMatchers.eq(TestVariables.user().userId),
        any[CartItem])).thenReturn(Future.failed(new ProductQuantityUnavailableException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId},
           |    "quantity": ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/cart/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.BAD_REQUEST)
      }
    }

  }
}
