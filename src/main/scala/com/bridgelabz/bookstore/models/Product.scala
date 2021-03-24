package com.bridgelabz.bookstore.models

case class Product(productId: Int,
                   author: String,
                   title: String,
                   image: String,
                   quantity: Int,
                   price: Double,
                   description: String
                  )
