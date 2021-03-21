package com.bridgelabz.bookstoretest.managers

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.managers.ProductManager
import com.bridgelabz.bookstore.exceptions.ProductDoesNotExistException
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.mockito.MockitoSugar
import com.bridgelabz.bookstore.models.{Product, User}
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.matchers.should.Matchers.{a, convertToAnyShouldWrapper}

import concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.{Await, Future}

class ProductManagerTest extends AnyFlatSpec with MockitoSugar {
  val iCrudProductMock: ICrud[Product] = mock[ICrud[Product]]
  val iCrudUserMock: ICrud[User] = mock[ICrud[User]]
  val productManager: ProductManager = new ProductManager(iCrudProductMock,iCrudUserMock)

  "Add Product" should "return true if product added successfully" in {
    when(iCrudProductMock.create(TestVariables.product())).thenReturn(Future(true))
    assert(Await.result(productManager.addProduct(TestVariables.user().userId,TestVariables.product()),1500.seconds))
  }

  "Get product" should "return true if product fetched successfully" in {
    when(iCrudProductMock.read()).thenReturn(Future[Seq[Product]](Seq[Product](TestVariables.product())))
    assert(Await.result(productManager.getProduct(Some("Xrnes")),1500.seconds)
      == Product(530,"Xrnes","TestProduct","12323434",2,3000.0,"This is a test product"))
  }

  "Get Product which doesn't exist" should "return Product Not found exception" in {
    val productTest = productManager.getProduct(Some("Any"))
    ScalaFutures.whenReady(productTest.failed){
      e => e shouldBe a [ProductDoesNotExistException]
    }
  }
}
