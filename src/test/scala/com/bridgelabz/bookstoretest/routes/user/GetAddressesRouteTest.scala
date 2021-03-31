package com.bridgelabz.bookstoretest.routes.user

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, UnverifiedAccountException}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.routes.UserRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class GetAddressesRouteTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {

  val mockUserManager: UserManager = mock[UserManager]
  val route: Route = new UserRoutes(mockUserManager).getAddresses

  "This service" should {
    "Route should fetch all addresses from a user account for Get request to /address" in {

      when(mockUserManager.getAddresses(TestVariables.user().userId))
        .thenReturn(Future.successful(Seq()))

      Get("/address") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.OK)
        }
    }

    "Route should fail to fetch addresses from a user account for Get request to /address if user-does-not-exist" in {

      when(mockUserManager.getAddresses(TestVariables.user().userId))
        .thenReturn(Future.failed(new AccountDoesNotExistException))

      Get("/address") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

    "Route should fail to fetch addresses from a user account for Get request to /address if user-is-unverified" in {

      when(mockUserManager.getAddresses(TestVariables.user().userId))
        .thenReturn(Future.failed(new UnverifiedAccountException))

      Get("/address") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

    "Route should fail to fetch addresses from a user account for Get request to /address if some internal error occurred" in {

      when(mockUserManager.getAddresses(TestVariables.user().userId))
        .thenReturn(Future.failed(new Exception))

      Get("/address") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.INTERNAL_SERVER_ERROR)
        }
    }


    "Route should fail to fetch addresses from a user account for Get request to /address if invalid token is provided" in {

      Get("/address") ~>
        addCredentials(OAuth2BearerToken("invalid")) ~>
        route ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

  }
}
