package com.bridgelabz.bookstoretest.database.mysql

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.ProductTable
import com.bridgelabz.bookstore.models.Product
import com.bridgelabz.bookstoretest.TestVariables
import com.dimafeng.testcontainers.{ForAllTestContainer, MySQLContainer}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class ProductsTest extends AnyFlatSpec with ForAllTestContainer with Matchers {

  override val container: MySQLContainer = MySQLContainer()
  val productCollection: ICrud[Product] = new ProductTable("productTest")
  Class.forName(container.driverClassName)

  it should "add a single product to the table" in {
    assert(Await.result(productCollection.create(
      TestVariables.product()), 1500.seconds).asInstanceOf[Boolean])
  }
  it should "fail to add a single product to the table" in {
    val createTest = productCollection.create(TestVariables.product())
    ScalaFutures.whenReady(createTest.failed){
      e => e shouldBe a[Exception]
    }
  }

  it should "read the products from the table" in {
    assert(Await.result(productCollection.read(), 1500.seconds).length === 1)
  }

  it should "update the product in the table" in {
    assert(Await.result(productCollection.update(
        TestVariables.product().title,TestVariables.product(),"title"
      ), 1500.seconds).asInstanceOf[Boolean])
  }
  it should "fail to update the product in the table" in {
    val updateTest = productCollection.update(
      "Test",TestVariables.product(),"title")
    ScalaFutures.whenReady(updateTest.failed){
      e => e shouldBe a[Exception]
    }
  }

  it should "delete the product from the table" in {
    assert(Await.result(productCollection.delete(
      TestVariables.product().title,"title"
    ), 1500.seconds).asInstanceOf[Boolean])
  }
  it should "fail to delete the product from the table" in {
    val deleteTest = productCollection.delete(
      TestVariables.product().title,"title")
    ScalaFutures.whenReady(deleteTest.failed){
      e => e shouldBe a[Exception]
    }
  }

}
