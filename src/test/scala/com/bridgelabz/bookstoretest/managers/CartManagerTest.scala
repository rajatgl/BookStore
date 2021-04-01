package com.bridgelabz.bookstoretest.managers

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.upgraded.CartManager
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.models.{Cart, Price, Product, User}
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{a, convertToAnyShouldWrapper}
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class CartManagerTest extends AnyFlatSpec with MockitoSugar {

  val userCollectionMock: ICrudRepository[User] = mock[ICrudRepository[User]]
  val productCollectionMock: ICrudRepository[Product] = mock[ICrudRepository[Product]]
  val cartCollectionMock: ICrudRepository[Cart] = mock[ICrudRepository[Cart]]

  val cartManager: CartManager = new CartManager(cartCollectionMock,userCollectionMock,productCollectionMock)

  "Add item" should "return failed future with AccountDoesNotExistException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq()))

    val result = cartManager.addItem(TestVariables.user().userId,
      TestVariables.cart().items.head)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[AccountDoesNotExistException]
    }
  }

  "Add item" should "return failed future with UnverifiedAccountException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user())))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq()))

    val result = cartManager.addItem(TestVariables.user().userId,
      TestVariables.cart().items.head)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[UnverifiedAccountException]
    }
  }

  "Add item" should "return failed future with ProductDoesNotExistException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq()))

    val result = cartManager.addItem(TestVariables.user().userId,
      TestVariables.cart().items.head)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[ProductDoesNotExistException]
    }
  }

  "Add item" should "return failed future with ProductQuantityUnavailableException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.cart())))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq(TestVariables.product(quantity = 0))))

    val result = cartManager.addItem(TestVariables.user().userId,
      TestVariables.cart().items.head)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[ProductQuantityUnavailableException]
    }
  }

  "Add item" should "return future of true if item is successfully added to the cart" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.cart())))

    when(productCollectionMock.read(TestVariables.product().productId,
      "productId")).thenReturn(Future(Seq(TestVariables.product())))

    when(cartCollectionMock.create(TestVariables.cart())).thenReturn(Future.successful(true))

    val result = cartManager.addItem(TestVariables.user().userId,
      TestVariables.cart().items.head)

    assert(Await.result(result, Duration.Inf))
  }

  "Get item" should "return failed future with AccountDoesNotExistException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = cartManager.getItems(TestVariables.user().userId)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[AccountDoesNotExistException]
    }
  }

  "Get item" should "return failed future with UnverifiedAccountException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user())))

    val result = cartManager.getItems(TestVariables.user().userId)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[UnverifiedAccountException]
    }
  }



  "Get item" should "return future of Sequence of cart products" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read()).thenReturn(Future(Seq(TestVariables.product())))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.cart())))

    val result = cartManager.getItems(TestVariables.user().userId)

    assert(Await.result(result, Duration.Inf).nonEmpty)
  }

  "Remove item" should "return failed future with AccountDoesNotExistException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = cartManager.removeItem(TestVariables.user().userId,
      TestVariables.cart().items.head.productId)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[AccountDoesNotExistException]
    }
  }

  "Remove item" should "return failed future with UnverifiedAccountException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user())))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = cartManager.removeItem(TestVariables.user().userId,
      TestVariables.cart().items.head.productId)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[UnverifiedAccountException]
    }
  }

  "Remove item" should "return failed future with CartDoesNotExistException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = cartManager.removeItem(TestVariables.user().userId,
      TestVariables.cart().items.head.productId)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[CartDoesNotExistException]
    }
  }

  "Remove item" should "return failed future with ProductDoesNotExistException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.cart())))

    val result = cartManager.removeItem(TestVariables.user().userId,1)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[ProductDoesNotExistException]
    }
  }

  "Remove item" should "return future of true if item is successfully removed from cart" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.cart())))

    when(cartCollectionMock.delete(TestVariables.user().userId,"userId")).thenReturn(Future.successful(true))

    val result = cartManager.removeItem(TestVariables.user().userId,
      TestVariables.cart().items.head.productId)

    assert(Await.result(result, Duration.Inf))
  }

  "Get price" should "return future of price of cart products" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))

    when(productCollectionMock.read()).thenReturn(Future(Seq(TestVariables.product())))

    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.cart())))

    val result = cartManager.getPrice(TestVariables.user().userId)

    assert(Await.result(result, Duration.Inf).isInstanceOf[Price])
  }

}
