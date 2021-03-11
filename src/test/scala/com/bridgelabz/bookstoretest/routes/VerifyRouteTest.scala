package com.bridgelabz.bookstoretest.routes

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.bridgelabz.bookstore.database.managers.UserManager
import com.bridgelabz.bookstore.routes.UserRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class VerifyRouteTest
  extends AnyWordSpec
    with ScalatestRouteTest
    with MockitoSugar
    with ScalaFutures {
  val mockUserManager: UserManager = mock[UserManager]
  lazy val route: Route = new UserRoutes(mockUserManager).verifyRoute

  "This service" should {
    "Route should verify a user account for Get request to /verify" in {
      when(mockUserManager.verifyOpt(
        TestVariables.otp())).thenReturn(Future.successful(true))

      Get(s"/verify?otp=${TestVariables.otp().data}&email=${TestVariables.otp().email}") ~> route ~>
        check {
          assert(status === StatusCodes.OK)
        }
    }

    "Route should not verify a user account for Get request to /verify if otp & email did not match" in {
      when(mockUserManager.verifyOpt(
        TestVariables.otp())).thenReturn(Future.successful(false))

      Get(s"/verify?otp=${TestVariables.otp().data}&email=${TestVariables.otp().email}") ~> route ~>
        check {
          assert(status === StatusCodes.BAD_REQUEST)
        }
    }

    "Route should not verify a user account for Get request to /verify if some error occurred" in {
      when(mockUserManager.verifyOpt(
        TestVariables.otp())).thenReturn(Future.failed(new Exception))

      Get(s"/verify?otp=${TestVariables.otp().data}&email=${TestVariables.otp().email}") ~> route ~>
        check {
          assert(status === StatusCodes.INTERNAL_SERVER_ERROR)
        }
    }
  }
}
