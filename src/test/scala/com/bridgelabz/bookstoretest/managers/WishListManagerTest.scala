package com.bridgelabz.bookstoretest.managers

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.upgraded.WishListManager
import com.bridgelabz.bookstore.exceptions.{AccountDoesNotExistException, UnverifiedAccountException}
import com.bridgelabz.bookstore.models.{User, WishList}
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{a, convertToAnyShouldWrapper}
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class WishListManagerTest extends AnyFlatSpec with MockitoSugar {

  val userCollectionMock: ICrudRepository[User] = mock[ICrudRepository[User]]
  val wishListCollectionMock: ICrudRepository[WishList] = mock[ICrudRepository[WishList]]

  val wishListManager: WishListManager = new WishListManager(wishListCollectionMock,userCollectionMock)

  "Add item" should "return failed future with AccountDoesNotExistException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.addItem(TestVariables.user().userId,
      TestVariables.wishList().items.head)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[AccountDoesNotExistException]
    }
  }

  "Add item" should "return failed future with UnverifiedAccountException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user())))

    val result = wishListManager.addItem(TestVariables.user().userId,
      TestVariables.wishList().items.head)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[UnverifiedAccountException]
    }

  }

  "Add item" should "return future of true if user does not have a wishlist" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.addItem(TestVariables.user().userId,
      TestVariables.wishList().items.head)
    assert(Await.result(result, Duration.Inf))
  }

  "Add item" should "return future of true if user has a wishlist in the database" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.wishList())))

    val result = wishListManager.addItem(TestVariables.user().userId,
      TestVariables.wishList().items.head)
    assert(Await.result(result, Duration.Inf))
  }

  "Remove item" should "return failed future with AccountDoesNotExistException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.removeItem(TestVariables.user().userId,
      TestVariables.wishList().items.head.product.productId)

    ScalaFutures.whenReady(result.failed) {
      exception => exception shouldBe a[AccountDoesNotExistException]
    }

  }

  "Remove item" should "return failed future with UnverifiedAccountException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user())))

    val result = wishListManager.removeItem(TestVariables.user().userId,
      TestVariables.wishList().items.head.product.productId)

    ScalaFutures.whenReady(result.failed) {
      exception => exception shouldBe a[UnverifiedAccountException]
    }

  }

  "Remove item" should "return future of true if item does not exist in the wishlist" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.removeItem(TestVariables.user().userId,
      TestVariables.wishList().items.head.product.productId)
    assert(Await.result(result, Duration.Inf))
  }

  "Remove item" should "return future of true if item got successfully removed from the wishlist" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.wishList())))

    val result = wishListManager.removeItem(TestVariables.user().userId,
      TestVariables.wishList().items.head.product.productId)
    assert(Await.result(result, Duration.Inf))
  }

  "Get item" should "return future of sequence of items from the wishlist" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.wishList())))

    val result = wishListManager.getItems(TestVariables.user().userId)
    assert(Await.result(result, Duration.Inf).nonEmpty)
  }


}
