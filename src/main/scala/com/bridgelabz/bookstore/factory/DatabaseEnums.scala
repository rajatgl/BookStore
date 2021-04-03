package com.bridgelabz.bookstore.factory

object DatabaseEnums extends Enumeration {

  val MONGODB_USER, MONGODB_PRODUCT, MONGODB_CART, MONGODB_WISHLIST, MONGODB_OTP,
  MYSQL_USER, MYSQL_PRODUCT, MYSQL_CART, MYSQL_WISHLIST = Value
}
