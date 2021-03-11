package com.bridgelabz.bookstoretest.database.mongodb

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseConfig}
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.flatspec.AnyFlatSpec
import scala.concurrent.duration._
import com.bridgelabz.bookstore.models.Product

import scala.concurrent.Await

class ProductCollectionTest extends AnyFlatSpec{
  val productCollection: ICrud[Product] = new DatabaseConfig[Product]("ProductTest", CodecRepository.PRODUCT)

  "When product is created" should "return Future of Completed " in {
    val product = TestVariables.product()
    val createTest = Await.result(productCollection.create(product), 1500.seconds)
    assert(createTest.toString === "The operation completed successfully")
  }

  "When searched for otp list" should "return Future " in {
    val readTest = Await.result(productCollection.read(), 1500.seconds)
    assert(readTest.length === 1)
  }

  "When updated otp using email" should "return Future of Updated " in {
    val product = TestVariables.product()
    val updateTest = Await.result(productCollection.update(TestVariables.product().productId, product, "productId"), 1500.seconds)
    assert(updateTest.toString === "AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}")
  }

  "When deleted otp" should "return Future of Deleted " in {
    val deleteTest = Await.result(productCollection.delete(TestVariables.product().productId, "productId"), 1500.seconds)
    assert(deleteTest.toString === "AcknowledgedDeleteResult{deletedCount=1}")
  }
}
