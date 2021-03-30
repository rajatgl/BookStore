package com.bridgelabz.bookstoretest.database.mongodb

import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection}
import com.bridgelabz.bookstore.models.WishList
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.flatspec.AnyFlatSpec
import concurrent.duration._
import scala.concurrent.Await

class WishListCollectionTest extends AnyFlatSpec {
  val wishListCollection: DatabaseCollection[WishList] = new DatabaseCollection[WishList]("wishListTest", CodecRepository.WISHLIST)
  "When created wishlist" should "return Future of Completed " in {
    val wishlist = TestVariables.wishList()
    val createTest = Await.result(wishListCollection.create(wishlist), 1500.seconds)
    assert(createTest.toString === "The operation completed successfully")
  }
  "When searched for wishlist" should "return Future " in {
    val readTest = Await.result(wishListCollection.read(), 1500.seconds)
    assert(readTest.length === 1)
  }
  "When updated wishlist" should "return Future of Updated " in {
    val wishlist = TestVariables.wishList()
    val updateTest = Await.result(wishListCollection.update(TestVariables.wishList().userId, wishlist, "userId"), 1500.seconds)
    assert(updateTest.toString === "AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}")
  }
  "When deleted wishlist" should "return Future of Deleted " in {
    val deleteTest = Await.result(wishListCollection.delete(TestVariables.wishList().userId, "userId"), 1500.seconds)
    assert(deleteTest.toString === "AcknowledgedDeleteResult{deletedCount=1}")
  }
}
