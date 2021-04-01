package com.bridgelabz.bookstoretest.routes.wishlist

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.database.managers.upgraded.WishListManager
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.routes.WishListRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class AddItemToCartRouteTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {

  val mockWishListManager: WishListManager = mock[WishListManager]
  lazy val route: Route = new WishListRoutes(mockWishListManager).addItemToCart

  "This service" should {

    """Route should add item to the cart from
      | wish-list for Post request to /wishlist/cart""".stripMargin in {

      when(mockWishListManager.addItemToCart(TestVariables.user().userId,
        TestVariables.product().productId,
        TestVariables.cart().items.head.quantity)).thenReturn(Future.successful(true))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId},
           |    "quantity" : ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/cart",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    """Route should fail to add item to the cart from wish-list for
      | Post request to /wishlist/cart
      | with AccountDoesNotExistException""".stripMargin in {

      when(mockWishListManager.addItemToCart(TestVariables.user().userId,
        TestVariables.product().productId,
        TestVariables.cart().items.head.quantity
      )).thenReturn(Future.failed(new AccountDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId},
           |    "quantity" : ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/cart",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    """Route should fail to add item to the cart from wish-list for
      | Post request to /wishlist/cart
      | with UnverifiedAccountException""".stripMargin in {

      when(mockWishListManager.addItemToCart(TestVariables.user().userId,
        TestVariables.product().productId,
        TestVariables.cart().items.head.quantity
      )).thenReturn(Future.failed(new UnverifiedAccountException))

      val jsonRequest = ByteString(
        s"""
    |{
      |    "productId": ${TestVariables.wishList().items.head.productId},
      |    "quantity" : ${TestVariables.cart().items.head.quantity}
      |}
    |""".stripMargin
    )

    val postRequest = HttpRequest(
      HttpMethods.POST,
      uri = "/wishlist/cart",
      entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
    )

    val authorization = TokenManager.generateToken(TestVariables.user().userId)

    postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
      assert(status === StatusCodes.UNAUTHORIZED)
    }
  }

    """Route should fail to add item to the cart from wish-list for
      | Post request to /wishlist/cart
      | with ProductDoesNotExistException""".stripMargin in {

      when(mockWishListManager.addItemToCart(TestVariables.user().userId,
        TestVariables.product().productId,
        TestVariables.cart().items.head.quantity
      )).thenReturn(Future.failed(new ProductDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId},
           |    "quantity" : ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/cart",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }

    """Route should fail to add item to the cart from wish-list for
      | Post request to /wishlist/cart
      | with WishListDoesNotExistException""".stripMargin in {

      when(mockWishListManager.addItemToCart(TestVariables.user().userId,
        TestVariables.product().productId,
        TestVariables.cart().items.head.quantity
      )).thenReturn(Future.failed(new WishListDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId},
           |    "quantity" : ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/cart",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }

    """Route should fail to add item to the cart from wish-list for
      | Post request to /wishlist/cart
      | with ProductQuantityUnavailableException""".stripMargin in {

      when(mockWishListManager.addItemToCart(TestVariables.user().userId,
        TestVariables.product().productId,
        TestVariables.cart().items.head.quantity
      )).thenReturn(Future.failed(new ProductQuantityUnavailableException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId},
           |    "quantity" : ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/cart",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.BAD_REQUEST)
      }
    }

  }
}
