package com.bridgelabz.bookstoretest.database.mongodb

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection}
import com.bridgelabz.bookstore.models.User
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.Await
import scala.concurrent.duration._

class UserCollectionTest extends AnyFlatSpec {
  val userCollection: ICrud[User] = new DatabaseCollection[User]("usersTest", CodecRepository.USER)

  "When created user" should "return Future of Completed " in {
    val user = TestVariables.user()
    val createTest = Await.result(userCollection.create(user), 1500.seconds)
    assert(createTest.toString === "The operation completed successfully")
  }
  "When searched for user" should "return Future " in {
    val readTest = Await.result(userCollection.read(), 1500.seconds)
    assert(readTest.length === 1)
  }
  "When updated user using email" should "return Future of Updated " in {
    val user = TestVariables.user()
    val updateTest = Await.result(userCollection.update(TestVariables.user().email, user, "email"), 1500.seconds)
    assert(updateTest.toString === "AcknowledgedUpdateResult{matchedCount=1, modifiedCount=1, upsertedId=null}")
  }
  "When deleted user" should "return Future of Deleted " in {
    val deleteTest = Await.result(userCollection.delete(TestVariables.user().email, "email"), 1500.seconds)
    assert(deleteTest.toString === "AcknowledgedDeleteResult{deletedCount=1}")
  }

}

