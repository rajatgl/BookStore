package com.bridgelabz.bookstoretest

import com.bridgelabz.bookstore.models.{Address, Cart, CartItem, Order, Otp, Price, Product, User, WishList, WishListItem}

/**
 * Created on 3/5/2021.
 * Class: TestVariables.scala
 * Author: Rajat G.L.
 */

// scalastyle:off
object TestVariables {
  def user(userId: String = "test502",
           userName: String = "Test",
           mobileNumber: String = "1234567891",
           addresses: Seq[Address] = Seq(address()),
           email: String = "test@test.com",
           password: String = "",
           verificationComplete: Boolean = false): User =

    User(userId, userName, mobileNumber, addresses, email, password, verificationComplete)

  def address(apartmentNumber: String = "1",
              apartmentName: String = "Hello",
              streetAddress: String = "World",
              landMark: String = "Goodbye",
              state: String = "Universe",
              pinCode: String = "000000"): Address =

    Address(apartmentNumber, apartmentName, streetAddress, landMark, state, pinCode)

  def otp(data: Int = 123,
          email: String = "test@test.com"): Otp =

    Otp(data,email)

  def product(productId: Int = 530,
              author: String = "Xrnes",
              title: String = "HiBook",
              image: String = "12323434",
              quantity: Int = 2,
              price: Double = 3000,
              description: String = "This is a test product"): Product =

    Product(productId,author,title,image,quantity,price,description)


  def wishList(userId: String = "moc.liamg@69dtrihcur",
               items: Seq[WishListItem] = Seq(WishListItem(1,
                 1616838811))): WishList =

    WishList(userId, items)

  def cart(userId: String = "moc.liamg@69dtrihcur",
           items: Seq[CartItem] = Seq(CartItem(1,
             1616838811,1))): Cart =

    Cart(userId,items)

  def cartTest(userId: String = user().userId,
           items: Seq[CartItem] = Seq(CartItem(product().productId,
             1616838811,1))): Cart =

    Cart(userId,items)

  def wishListTest(userId: String = user().userId,
               items: Seq[WishListItem] = Seq(WishListItem(product().productId,
                 1616838811))): WishList =

    WishList(userId, items)

  def price(totalPrice: Double = 3000,
            taxPrice: Double = 390,
            grandTotal: Double = 3390): Price =

    Price(totalPrice,taxPrice,grandTotal)

  def order(userId: String = user().userId,
            orderId: String = "test",
            transactionId: String = "test",
            deliveryAddress: Address = `address`(),
            items: Seq[CartItem] = cartTest().items,
            status: String = "order placed",
            orderTimestamp: Long = 1616838811,
            deliveryTimestamp: Long = 1616838811 + (5*24*60*60*1000)): Order =

    Order(userId,orderId,transactionId,deliveryAddress,items,status,orderTimestamp,deliveryTimestamp)

}
