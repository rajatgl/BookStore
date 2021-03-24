package com.bridgelabz.bookstore.models

case class Cart(cartId: String, userId: String, items: Seq[CartItem])
