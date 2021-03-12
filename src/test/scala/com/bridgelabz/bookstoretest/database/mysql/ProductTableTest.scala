package com.bridgelabz.bookstoretest.database.mysql

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.ProductTable
import com.bridgelabz.bookstore.models.Product
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class ProductTableTest extends AnyFlatSpec {

  val productCollection: ICrud[Product] = new ProductTable("productTest")

  "When product is created" should "return Future of true " in {
    assert(Await.result(productCollection.create(TestVariables.product()), 1500.seconds).asInstanceOf[Boolean])
  }

  "When product is read" should "return Future of sequence " in {
    assert(Await.result(productCollection.read(), 1500.seconds).length === 1)
  }

  "When product is updated" should "return Future of true " in {
    assert(Await.result(productCollection.update(TestVariables.product().title,TestVariables.product(),"title"), 1500.seconds).asInstanceOf[Boolean])
  }

  "When product is deleted" should "return Future of true " in {
    assert(Await.result(productCollection.delete(TestVariables.product().title,"title"), 1500.seconds).asInstanceOf[Boolean])
  }

}
