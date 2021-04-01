
package com.bridgelabz.bookstoretest.managers

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.upgraded.WishListManager
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.models.{Cart, Product, User, WishList}
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
  val productCollectionMock: ICrudRepository[Product] = mock[ICrudRepository[Product]]
  val cartCollectionMock: ICrudRepository[Cart] = mock[ICrudRepository[Cart]]

  val wishListManager: WishListManager = new WishListManager(wishListCollectionMock, userCollectionMock, productCollectionMock, cartCollectionMock)

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

  "Add item" should "return failed future with ProductDoesNotExistException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq[Product]()))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.addItem(TestVariables.user().userId,
      TestVariables.wishList().items.head)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[ProductDoesNotExistException]
    }
  }

  "Add item" should "return future of true if user does not have a wishlist" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq(TestVariables.product())))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.addItem(TestVariables.user().userId,
      TestVariables.wishList().items.head)
    assert(Await.result(result, Duration.Inf))
  }

  "Add item" should "return future of true if user has a wishlist in the database" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq(TestVariables.product())))

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
      TestVariables.wishList().items.head.productId)

    ScalaFutures.whenReady(result.failed) {
      exception => exception shouldBe a[AccountDoesNotExistException]
    }

  }

  "Remove item" should "return failed future with UnverifiedAccountException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user())))

    val result = wishListManager.removeItem(TestVariables.user().userId,
      TestVariables.wishList().items.head.productId)

    ScalaFutures.whenReady(result.failed) {
      exception => exception shouldBe a[UnverifiedAccountException]
    }

  }

  "Remove item" should "return failed future with WishListDoesNotExistException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.removeItem(TestVariables.user().userId,
      TestVariables.wishList().items.head.productId)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[WishListDoesNotExistException]
    }
  }

  "Remove item" should "return failed future with ProductDoesNotExistException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.wishList())))

    val result = wishListManager.removeItem(TestVariables.user().userId,1)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[ProductDoesNotExistException]
    }
  }

  "Remove item" should "return future of true if item got successfully removed from the wishlist" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.wishList())))

    val result = wishListManager.removeItem(TestVariables.user().userId,
      TestVariables.wishList().items.head.productId)
    assert(Await.result(result, Duration.Inf))
  }

  "Get item" should "return failed future with AccountDoesNotExistException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.getItems(TestVariables.user().userId)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[AccountDoesNotExistException]
    }
  }

  "Get item" should "return failed future with UnverifiedAccountException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user())))

    val result = wishListManager.getItems(TestVariables.user().userId)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[UnverifiedAccountException]
    }
  }



  "Get item" should "return future of empty sequence of items for an empty wishlist" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read()).thenReturn(Future(Seq(TestVariables.product())))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.getItems(TestVariables.user().userId)

    assert(Await.result(result, Duration.Inf).isEmpty)
  }

  "Get item" should "return future of sequence of items from the wishlist" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read()).thenReturn(Future(Seq(TestVariables.product())))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.wishList())))

    val result = wishListManager.getItems(TestVariables.user().userId)
    assert(Await.result(result, Duration.Inf).nonEmpty)
  }

  "Add to cart" should "return failed future with AccountDoesNotExistException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.addItemToCart(TestVariables.user().userId,TestVariables.product().productId,1)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[AccountDoesNotExistException]
    }
  }

  "Add to cart" should "return failed future with UnverifiedAccountException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user())))

    val result = wishListManager.addItemToCart(TestVariables.user().userId,TestVariables.product().productId,1)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[UnverifiedAccountException]
    }
  }


  "Add to cart" should "return failed future with WishListDoesNotExistException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq(TestVariables.product())))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.addItemToCart(TestVariables.user().userId,TestVariables.product().productId,1)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[WishListDoesNotExistException]
    }
  }

  "Add to cart" should "return failed future with ProductDoesNotExistException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read(2,
      "productId")).thenReturn(Future(Seq()))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.wishList())))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.addItemToCart(TestVariables.user().userId,
      2,1)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[ProductDoesNotExistException]
    }
  }

  "Add to cart" should "return failed future with ProductQuantityUnavailableException" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq()))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.wishList())))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = wishListManager.addItemToCart(TestVariables.user().userId,
      TestVariables.product().productId,1)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[ProductQuantityUnavailableException]
    }
  }

  "Add to cart" should "return future of true if wishlist-item is added to the cart" in {
    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq(TestVariables.product())))

    when(wishListCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.wishList())))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    when(cartCollectionMock.create(TestVariables.cart())).thenReturn(Future(true))

    val result = wishListManager.addItemToCart(TestVariables.user().userId,
      TestVariables.product().productId,1)

    assert(Await.result(result, Duration.Inf))
  }
}