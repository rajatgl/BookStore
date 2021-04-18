package com.bridgelabz.bookstoretest.routes.order

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.bridgelabz.bookstore.database.managers.upgraded.OrderManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, UnverifiedAccountException}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.routes.OrderRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class GetOrdersRouteTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {

  val mockOrderManager: OrderManager = mock[OrderManager]
  lazy val route: Route = new OrderRoutes(mockOrderManager).getOrders

  "This service" should {

    """Route should fail to fetch user orders for
      | Get request to /orders
      | with AccountDoesNotExistException""".stripMargin in {

      when(mockOrderManager.getOrders(TestVariables.user().userId,None))
        .thenReturn(Future.failed(new AccountDoesNotExistException))

      Get("/orders") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

    """Route should fail to fetch user orders for
      | Get request to /orders
      | with UnverifiedAccountException""".stripMargin in {

      when(mockOrderManager.getOrders(TestVariables.user().userId,None))
        .thenReturn(Future.failed(new UnverifiedAccountException))

      Get("/orders") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }


    "Route should fetch all user orders for Get request to /orders" in {

      when(mockOrderManager.getOrders(TestVariables.user().userId,None))
        .thenReturn(Future.successful(Seq()))

      Get("/orders") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        route ~>
        check {
          status.equals(StatusCodes.OK)
        }
    }

  }

}
