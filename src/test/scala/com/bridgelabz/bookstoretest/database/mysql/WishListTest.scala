package com.bridgelabz.bookstoretest.database.mysql

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.models.WishList
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.bridgelabz.bookstore.database.mysql.tables.WishListTable
import com.bridgelabz.bookstoretest.TestVariables
import concurrent.duration._
import scala.concurrent.Await

class WishListTest extends AnyFlatSpec with Matchers{
  val wishListTable : ICrud[WishList] = new WishListTable("wishListTest","products")
  it should "add wishlist to the table" in {
    assert(Await.result(wishListTable.create(
      TestVariables.wishList()),1500.seconds).asInstanceOf[Boolean])
  }

  it should "read the wishlist details from the table" in {
    assert(Await.result(wishListTable.read(), 1500.seconds).length === 2)
  }

  it should "update the wishlist in the table" in {
    assert(Await.result(wishListTable.update(
      TestVariables.wishList().userId,TestVariables.wishList(),"userId"
    ), 1500.seconds).asInstanceOf[Boolean])
  }


  it should "delete the wishlist from the table" in {
    assert(Await.result(wishListTable.delete(
      TestVariables.wishList().userId,"userId"
    ), 1500.seconds).asInstanceOf[Boolean])
  }
}