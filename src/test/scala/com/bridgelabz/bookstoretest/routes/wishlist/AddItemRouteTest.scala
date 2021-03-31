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
import com.bridgelabz.bookstore.models.WishListItem
import com.bridgelabz.bookstore.routes.WishListRoutes
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

  val mockWishListManager: WishListManager = mock[WishListManager]
  lazy val routes: Route = new WishListRoutes(mockWishListManager).addItem

  "This service" should {
    "Route should add item to a wish-list for Post request to /wishlist/item" in {
      when(mockWishListManager.addItem(
        org.mockito.ArgumentMatchers.eq(TestVariables.user().userId),
        any[WishListItem])).thenReturn(Future.successful(true))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> routes ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "Route should fail to add item to a wish-list for Post request to /wishlist/item due to AccountDoesNotExistException" in {
      when(mockWishListManager.addItem(
        org.mockito.ArgumentMatchers.eq(TestVariables.user().userId),
        any[WishListItem])).thenReturn(Future.failed(new AccountDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> routes ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    "Route should fail to add item to a wish-list for Post request to /wishlist/item due to UnverifiedAccountException" in {
      when(mockWishListManager.addItem(
        org.mockito.ArgumentMatchers.eq(TestVariables.user().userId),
        any[WishListItem])).thenReturn(Future.failed(new UnverifiedAccountException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> routes ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    "Route should fail to add item to a wish-list for Post request to /wishlist/item due to ProductDoesNotExistException" in {
      when(mockWishListManager.addItem(
        org.mockito.ArgumentMatchers.eq(TestVariables.user().userId),
        any[WishListItem])).thenReturn(Future.failed(new ProductDoesNotExistException))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> routes ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }

  }
}
