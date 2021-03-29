package com.bridgelabz.bookstoretest.database.mysql

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.models.MySqlCart
import com.bridgelabz.bookstore.database.mysql.tables.MySqlCartTable
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import concurrent.duration._
import scala.concurrent.Await

class MySqlCartTest extends AnyFlatSpec with Matchers{
  val cartTable: ICrud[MySqlCart] = new MySqlCartTable("cartTest","userTest")
  it should "add a single product to the table" in {
    assert(Await.result(cartTable.create(
      TestVariables.product()), 1500.seconds).asInstanceOf[Boolean])
  }
  it should "fail to add a single product to the table" in {
    val createTest = cartTable.create(TestVariables.product())
    ScalaFutures.whenReady(createTest.failed){
      e => e shouldBe a[Exception]
    }
  }

  it should "read the products from the table" in {
    assert(Await.result(cartTable.read(), 1500.seconds).length === 1)
  }

  it should "update the product in the table" in {
    assert(Await.result(cartTable.update(
      TestVariables.product().title,TestVariables.product(),"title"
    ), 1500.seconds).asInstanceOf[Boolean])
  }
  it should "fail to update the product in the table" in {
    val updateTest = cartTable.update(
      "Test",TestVariables.product(),"title")
    ScalaFutures.whenReady(updateTest.failed){
      e => e shouldBe a[Exception]
    }
  }

  it should "delete the product from the table" in {
    assert(Await.result(cartTable.delete(
      TestVariables.product().title,"title"
    ), 1500.seconds).asInstanceOf[Boolean])
  }
  it should "fail to delete the product from the table" in {
    val deleteTest = cartTable.delete(
      TestVariables.product().title,"title")
    ScalaFutures.whenReady(deleteTest.failed){
      e => e shouldBe a[Exception]
    }
  }
}
