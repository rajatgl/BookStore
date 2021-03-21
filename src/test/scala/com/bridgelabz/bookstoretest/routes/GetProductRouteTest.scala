package com.bridgelabz.bookstoretest.routes

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.bridgelabz.bookstore.database.managers.ProductManager
import com.bridgelabz.bookstore.exceptions.ProductDoesNotExistException
import com.bridgelabz.bookstore.routes.ProductRoutes
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.Future

class GetProductRouteTest extends AnyWordSpec with ScalatestRouteTest with MockitoSugar with ScalaFutures{
  val mockProductManager: ProductManager = mock[ProductManager]
  lazy val routes: Route = new ProductRoutes(mockProductManager).getProductRoute

  "Route should return OK for Get request to /products with title" in {

    when(mockProductManager.getProduct(Some(TestVariables.product().title))).
      thenReturn(Future.successful(Seq()))

    Get("/products?name=TestProduct") ~> routes ~>
      check {
        assert(status == StatusCodes.OK)
      }
  }


  "Route should return OK for Get request to /products with author" in {

    when(mockProductManager.getProduct(Some(TestVariables.product().author))).
      thenReturn(Future.successful(Seq()))

    Get("/products?name=Xrnes") ~> routes ~>
      check {
        assert(status == StatusCodes.OK)
      }
  }

  "Route should return Not found for Get request to /products with Product which doesn't exists" in {
    when(mockProductManager.getProduct(Some("Random"))).
      thenReturn(Future.failed(new ProductDoesNotExistException))

    Get("/products?name=Random") ~> routes ~>
      check {
        assert(status == StatusCodes.NOT_FOUND)
      }
  }
}
