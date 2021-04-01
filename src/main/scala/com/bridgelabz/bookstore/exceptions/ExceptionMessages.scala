package com.bridgelabz.bookstore.exceptions

/**
 * Created on 3/6/2021.
 * Class: ExceptionMessages.scala
 * Author: Rajat G.L.
 */
object ExceptionMessages {
  val messages = Map(
    "ACCOUNT_DOES_NOT_EXIST" -> "ACCOUNT_DOES_NOT_EXIST: You are not authorized to access this content.",
    "PASSWORD_MISMATCH" -> "PASSWORD_MISMATCH: Please enter a valid password for given email.",
    "BAD_EMAIL" -> "BAD_EMAIL: Please enter a valid email address.",
    "UNVERIFIED_ACCOUNT" -> "UNVERIFIED_ACCOUNT: Please verify your email first to proceed.",
    "PRODUCT_DOES_NOT_EXIST" -> "PRODUCT NOT FOUND: Product you are searching for does not exists.",
    "PRODUCT_QUANTITY_UNAVAILABLE" -> "PRODUCT QUANTITY UNAVAILABLE: Product requested is not available in the required quantity.",
    "WISHLIST_DOES_NOT_EXIST" -> "WISHLIST DOES NOT EXIST: Wishlist requested does not exist.",
    "CART_DOES_NOT_EXIST" -> "CART DOES NOT EXIST: Cart requested does not exist."
  )
}
