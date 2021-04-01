package com.bridgelabz.bookstoretest.database.mongodb

import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection}
import com.bridgelabz.bookstore.models.Cart
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.flatspec.AnyFlatSpec
import concurrent.duration._
import scala.concurrent.Await

class CartCollectionTest extends AnyFlatSpec {
  val cartCollection: DatabaseCollection[Cart] = new DatabaseCollection[Cart]("cartTest", CodecRepository.CART)
  "When created cart" should "return Future of Completed " in {
    val cart = TestVariables.cart()
    val createTest = Await.result(cartCollection.create(cart), 1500.seconds)
    assert(createTest.toString === "The operation completed successfully")
  }
  "When searched for cart" should "return Future " in {
    val readTest = Await.result(cartCollection.read(), 1500.seconds)
    assert(readTest.length === 1)
  }
  "When updated cart" should "return Future of Updated " in {
    val cart = TestVariables.cart()
    val updateTest = Await.result(cartCollection.update(TestVariables.cart().userId, cart, "userId"), 1500.seconds)
    assert(updateTest.toString === "AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}")
  }
  "When deleted cart" should "return Future of Deleted " in {
    val deleteTest = Await.result(cartCollection.delete(TestVariables.cart().userId, "userId"), 1500.seconds)
    assert(deleteTest.toString === "AcknowledgedDeleteResult{deletedCount=1}")
  }
}
