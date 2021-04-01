package com.bridgelabz.bookstore.database.mysql.models

case class MySqlCartItem(userId : String,
                          timestamp : Long,
                         productId : Int,
                         quantity : Int)
