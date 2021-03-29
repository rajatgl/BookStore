package com.bridgelabz.bookstore.database.mysql.models

case class MySqlCartItem(cartId : String,
                         productId : Int,
                         timeStamp : Long,
                         quantity : Int)
