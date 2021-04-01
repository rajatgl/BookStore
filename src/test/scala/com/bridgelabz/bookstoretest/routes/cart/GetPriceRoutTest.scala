package com.bridgelabz.bookstoretest.routes.cart

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.bridgelabz.bookstore.database.managers.upgraded.CartManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, UnverifiedAccountException}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.routes.CartRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class GetPriceRoutTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {

  val mockCartManager: CartManager = mock[CartManager]
  lazy val route: Route = new CartRoutes(mockCartManager).getPrice

  "This service" should {

    """Route should fetch the bill of items in the cart for
      | Get request to /cart/price""".stripMargin in {

      when(mockCartManager.getPrice(TestVariables.user().userId))
        .thenReturn(Future.successful(TestVariables.price()))

      Get("/cart/price") ~>
        addCredentials(OAuth2BearerToken(
          TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.OK)
        }
    }

    """Route should fail to fetch the bill of items in the cart for
      | Get request to /cart/price
      | with AccountDoesNotExistException""".stripMargin in {

      when(mockCartManager.getPrice(TestVariables.user().userId))
        .thenReturn(Future.failed(new AccountDoesNotExistException))

      Get("/cart/price") ~>
        addCredentials(OAuth2BearerToken(
          TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

    """Route should fail to fetch the bill of items in the cart for
      | Get request to /cart/price
      | with UnverifiedAccountException""".stripMargin in {

      when(mockCartManager.getPrice(TestVariables.user().userId))
        .thenReturn(Future.failed(new UnverifiedAccountException))

      Get("/cart/price") ~>
        addCredentials(OAuth2BearerToken(
          TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

  }
}
