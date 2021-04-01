package com.bridgelabz.bookstore.models

case class Cart(userId: String, items: Seq[CartItem])
