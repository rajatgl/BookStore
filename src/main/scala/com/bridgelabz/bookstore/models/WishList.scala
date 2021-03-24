package com.bridgelabz.bookstore.models

case class WishList(wishListId: String, userId: String, items: Seq[CartItem])
