package com.bridgelabz.bookstoretest.managers

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.upgraded.OrderManager
import com.bridgelabz.bookstore.exceptions._
import com.bridgelabz.bookstore.models.{Cart, Order, Product, User}
import com.bridgelabz.bookstoretest.TestVariables
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{a, convertToAnyShouldWrapper}
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

class OrderManagerTest extends AnyFlatSpec with MockitoSugar {

  val orderCollectionMock: ICrudRepository[Order] = mock[ICrudRepository[Order]]
  val userCollectionMock: ICrudRepository[User] = mock[ICrudRepository[User]]
  val cartCollectionMock: ICrudRepository[Cart] = mock[ICrudRepository[Cart]]
  val productCollectionMock: ICrudRepository[Product] = mock[ICrudRepository[Product]]

  val orderManager: OrderManager = new OrderManager(orderCollectionMock,
    userCollectionMock,
    cartCollectionMock,
    productCollectionMock)

  "Place order" should "return failed future with AccountDoesNotExistException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))
    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))
    when(productCollectionMock.read()).thenReturn(Future(Seq()))

    val result = orderManager.placeOrder(TestVariables.user().userId,"xyz",0)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[AccountDoesNotExistException]
    }

  }

  "Place order" should "return failed future with UnverifiedAccountException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user())))
    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))
    when(productCollectionMock.read()).thenReturn(Future(Seq()))

    val result = orderManager.placeOrder(TestVariables.user().userId,"xyz",0)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[UnverifiedAccountException]
    }

  }

  "Place order" should "return failed future with AddressNotFoundException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(addresses = Seq(),
      verificationComplete = true))))
    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))
    when(productCollectionMock.read()).thenReturn(Future(Seq()))

    val result = orderManager.placeOrder(TestVariables.user().userId,"xyz",1)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[AddressNotFoundException]
    }

  }

  "Place order" should "return failed future with CartDoesNotExistException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))
    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))
    when(productCollectionMock.read()).thenReturn(Future(Seq()))

    val result = orderManager.placeOrder(TestVariables.user().userId,"xyz",0)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[CartDoesNotExistException]
    }

  }

  "Place order" should "return failed future with ProductDoesNotExistException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))
    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.cartTest())))
    when(productCollectionMock.read()).thenReturn(Future(Seq()))

    val result = orderManager.placeOrder(TestVariables.user().userId,"xyz",0)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[ProductDoesNotExistException]
    }

  }

  "Place order" should "return failed future with ProductQuantityUnavailableException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))
    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.cartTest())))
    when(productCollectionMock.read()).thenReturn(Future(Seq(TestVariables.product(quantity = 0))))

    val result = orderManager.placeOrder(TestVariables.user().userId,"xyz",0)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[ProductQuantityUnavailableException]
    }

  }

  "Place order" should "return orderId in some near future if order is placed successfully" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))
    when(cartCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.cartTest())))
    when(productCollectionMock.read()).thenReturn(Future(Seq(TestVariables.product())))
    when(productCollectionMock.update(TestVariables.product().productId,
      1,"productId","quantity")).thenReturn(Future(true))
    when(orderCollectionMock.create(any[Order])).thenReturn(Future("test"))

    val result = orderManager.placeOrder(TestVariables.user().userId,
      TestVariables.order().transactionId,0)

    assert(Await.result(result, Duration.Inf) == "test")

  }

  "Get orders" should "return failed future with AccountDoesNotExistException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq()))

    val result = orderManager.getOrders(TestVariables.user().userId,None)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[AccountDoesNotExistException]
    }

  }

  "Get orders" should "return failed future with UnverifiedAccountException" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user())))

    val result = orderManager.getOrders(TestVariables.user().userId,None)

    ScalaFutures.whenReady(result.failed){
      exception => exception shouldBe a[UnverifiedAccountException]
    }

  }

  "Get orders" should "return future of sequence of orders made by the user" in {

    when(userCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.user(verificationComplete = true))))
    when(orderCollectionMock.read(TestVariables.user().userId,
      "userId")).thenReturn(Future(Seq(TestVariables.order())))

    val result = orderManager.getOrders(TestVariables.user().userId,None)

    assert(Await.result(result, Duration.Inf).nonEmpty)

  }

}
