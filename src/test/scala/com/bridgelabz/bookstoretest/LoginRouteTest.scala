package com.bridgelabz.bookstoretest

import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.Main
import com.bridgelabz.bookstore.database.managers.UserManager
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class LoginRouteTest extends AnyWordSpec with ScalatestRouteTest with MockitoSugar with ScalaFutures {
  val mockUserManager: UserManager = mock[UserManager]

  "The service" should {
    "Routes should login a test account for a Post request to /login" in {
      when(mockUserManager.login(
        TestVariables.user().email, TestVariables.user().password)).thenReturn(Future.successful("TOKEN"))

      val jsonRequest = ByteString(
        s"""
            {
              "email": ${TestVariables.user().email},
              "password": ${TestVariables.user().password}
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> Main.route(mockUserManager) ~> check {
        //status.equals(StatusCodes.OK)
        println(response.toString())
        assert(header("Token").isDefined && header("Token").get.value.equals("TOKEN"))
      }
    }
  }
}