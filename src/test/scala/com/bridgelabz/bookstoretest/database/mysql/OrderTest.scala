package com.bridgelabz.bookstoretest.database.mysql

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.models.{Cart, Order}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.bridgelabz.bookstore.database.mysql.tables.{CartTable, OrderTable}
import com.bridgelabz.bookstoretest.TestVariables

import concurrent.duration._
import scala.concurrent.Await
class OrderTest extends AnyFlatSpec with Matchers{
  val orderTable : ICrud[Order] = new OrderTable("cartTest","productTest","userTestusers")
  it should "add details of order to the table" in {
    assert(Await.result(orderTable.create(
      TestVariables.order()),1500.seconds).asInstanceOf[Boolean])
  }

  it should "read the order details from the table" in {
    assert(Await.result(orderTable.read(), 1500.seconds).length === 2)
  }

  it should "update the order in the table" in {
    assert(Await.result(orderTable.update(
      TestVariables.order().userId,TestVariables.order(),"userId"
    ), 1500.seconds).asInstanceOf[Boolean])
  }


  it should "delete the order from the table" in {
    assert(Await.result(orderTable.delete(
      TestVariables.order().userId,"userId"
    ), 1500.seconds).asInstanceOf[Boolean])
  }
}