package com.bridgelabz.bookstoretest.routes.wishlist

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.bridgelabz.bookstore.database.managers.upgraded.WishListManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, UnverifiedAccountException}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.routes.WishListRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class GetItemsRouteTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {

  val mockWishListManager: WishListManager = mock[WishListManager]
  lazy val route: Route = new WishListRoutes(mockWishListManager).getItems

  "This service" should {

    "Route should fetch all items from wishlist for Get request to /wishlist/items" in {

      when(mockWishListManager.getItems(TestVariables.user().userId))
        .thenReturn(Future.successful(Seq()))

      Get("/wishlist/items") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.OK)
        }
    }

    "Route should fail to fetch items from the wishlist for Get request to /wishlist/items due to AccountDoesNotExistException" in {

      when(mockWishListManager.getItems(TestVariables.user().userId))
        .thenReturn(Future.failed(new AccountDoesNotExistException))

      Get("/wishlist/items") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

    "Route should fail to fetch items from the wishlist for Get request to /wishlist/items due to UnverifiedAccountException" in {

      when(mockWishListManager.getItems(TestVariables.user().userId))
        .thenReturn(Future.failed(new UnverifiedAccountException))

      Get("/wishlist/items") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

  }
}
