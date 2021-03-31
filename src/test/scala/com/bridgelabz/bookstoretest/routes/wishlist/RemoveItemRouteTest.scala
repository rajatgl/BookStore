package com.bridgelabz.bookstoretest.routes.wishlist

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.database.managers.upgraded.WishListManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, ProductDoesNotExistException, UnverifiedAccountException, WishListDoesNotExistException}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.routes.WishListRoutes
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

  val mockWishListManager: WishListManager = mock[WishListManager]
  lazy val route: Route = new WishListRoutes(mockWishListManager).removeItem

  "This service" should {
    "Route should remove an item to a wish-list for Delete request to /wishlist/remove" in {
      when(mockWishListManager.removeItem(TestVariables.user().userId,
        TestVariables.wishList().items.head.productId)).thenReturn(Future.successful(true))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/wishlist/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "Route should fail to remove an item to a wish-list for Delete request to /wishlist/remove due to AccountDoesNotExistException" in {
      when(mockWishListManager.removeItem(TestVariables.user().userId,
        TestVariables.wishList().items.head.productId)).thenReturn(Future.failed(new AccountDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/wishlist/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    "Route should fail to remove an item to a wish-list for Delete request to /wishlist/remove due to UnverifiedAccountException" in {
      when(mockWishListManager.removeItem(TestVariables.user().userId,
        TestVariables.wishList().items.head.productId)).thenReturn(Future.failed(new UnverifiedAccountException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/wishlist/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    "Route should fail to remove an item to a wish-list for Delete request to /wishlist/remove due to ProductDoesNotExistException" in {
      when(mockWishListManager.removeItem(TestVariables.user().userId,
        TestVariables.wishList().items.head.productId)).thenReturn(Future.failed(new ProductDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/wishlist/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }

    "Route should fail to remove an item to a wish-list for Delete request to /wishlist/remove due to WishListDoesNotExistException" in {
      when(mockWishListManager.removeItem(TestVariables.user().userId,
        TestVariables.wishList().items.head.productId)).thenReturn(Future.failed(new WishListDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/wishlist/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> route ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }
  }
}
