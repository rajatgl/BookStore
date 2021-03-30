package com.bridgelabz.bookstoretest.database.mysql

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.models.Cart
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.bridgelabz.bookstore.database.mysql.tables.CartTable
import com.bridgelabz.bookstoretest.TestVariables
import concurrent.duration._
import scala.concurrent.Await
class CartTest extends AnyFlatSpec with Matchers{
  val cartTable : ICrud[Cart] = new CartTable("cartTest","products")
  it should "add details of cart to the table" in {
    assert(Await.result(cartTable.create(
      TestVariables.cart()), 1500.seconds).asInstanceOf[Boolean])
  }

  it should "read the cart details from the table" in {
    assert(Await.result(cartTable.read(), 1500.seconds).length === 1)
  }

  it should "update the cart in the table" in {
    assert(Await.result(cartTable.update(
      TestVariables.cart().cartId,TestVariables.cart(),"cartId"
    ), 1500.seconds).asInstanceOf[Boolean])
  }


  it should "delete the cart from the table" in {
    assert(Await.result(cartTable.delete(
      TestVariables.cart().cartId,"cartId"
    ), 1500.seconds).asInstanceOf[Boolean])
  }
}
