package com.bridgelabz.bookstoretest.routes

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.database.managers.ProductManager
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.routes.ProductRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class AddProductRoutesTest extends AnyWordSpec with ScalatestRouteTest with MockitoSugar with ScalaFutures {

  val mockProductManager: ProductManager = mock[ProductManager]
  lazy val routes: Route = new ProductRoutes(mockProductManager).addProductRoute

  "This service" should {
    "Route should add product for Post request to /addProduct" in {
      when(mockProductManager.addProduct(
        TestVariables.user().userId,
        TestVariables.product())).thenReturn(Future.successful(true))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": "${TestVariables.product().productId}",
           |    "author": "${TestVariables.product().author}",
           |    "title": "${TestVariables.product().title}",
           |    "image": "${TestVariables.product().image}",
           |    "quantity": "${TestVariables.product().quantity}",
           |    "price":"${TestVariables.product().price}",
           |    "description":"${TestVariables.product().description}"
           |}
           |""".stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/addProduct",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )
      val authorization = TokenManager.generateToken(TestVariables.user().userId)
      postRequest ~> addHeader("Authorization", s"Bearer $authorization") ~> routes ~> check {
        assert(status === StatusCodes.OK)
      }
    }
  }

  "This service" should {
    "Route should Return Unauthorized for invalid token" in {
      when(mockProductManager.addProduct(
        TestVariables.user().userId,
        TestVariables.product())).thenReturn(Future.successful(true))

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": "${TestVariables.product().productId}",
           |    "author": "${TestVariables.product().author}",
           |    "title": "${TestVariables.product().title}",
           |    "image": "${TestVariables.product().image}",
           |    "quantity": "${TestVariables.product().quantity}",
           |    "price":"${TestVariables.product().price}",
           |    "description":"${TestVariables.product().description}"
           |}
           |""".stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/addProduct",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )
      postRequest ~> addHeader("Authorization", s"Bearer invalid") ~> routes ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }
  }


}
