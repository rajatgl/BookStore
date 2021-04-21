package com.bridgelabz.bookstoretest.database.mongodb

import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection}
import com.bridgelabz.bookstore.models.Order
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.flatspec.AnyFlatSpec
import concurrent.duration._
import scala.concurrent.Await

class OrderCollectionTest extends AnyFlatSpec {
  val orderCollection: DatabaseCollection[Order] = new DatabaseCollection[Order]("cartTest", CodecRepository.ORDER)
  "When created order" should "return Future of Completed " in {
    val order = TestVariables.order()
    val createTest = Await.result(orderCollection.create(order), 1500.seconds)
    assert(createTest.toString === "The operation completed successfully")
  }
  "When searched for order" should "return Future " in {
    val readTest = Await.result(orderCollection.read(), 1500.seconds)
    assert(readTest.length === 1)
  }
  "When updated order" should "return Future of Updated " in {
    val order = TestVariables.order()
    val updateTest = Await.result(orderCollection.update(TestVariables.order().userId, order, "userId"), 1500.seconds)
    assert(updateTest.toString === "AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}")
  }
  "When deleted order" should "return Future of Deleted " in {
    val deleteTest = Await.result(orderCollection.delete(TestVariables.order().userId, "userId"), 1500.seconds)
    assert(deleteTest.toString === "AcknowledgedDeleteResult{deletedCount=1}")
  }
}
