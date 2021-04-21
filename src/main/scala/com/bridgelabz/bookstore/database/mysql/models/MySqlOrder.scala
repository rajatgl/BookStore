package com.bridgelabz.bookstore.database.mysql.models

case class MySqlOrder(userId : String,
                      orderId: String,
                      transactionId: String,
                      status: String,
                      orderTimestamp: Long,
                      deliveryTimestamp: Long)
