package com.bridgelabz.bookstore.models

case class Order(userId: String,
                 orderId: String,
                 transactionId: String,
                 deliveryAddress: Seq[Address],
                 items: Seq[CartItem],
                 status: String,
                 orderTimestamp: Long,
                 deliveryTimestamp: Long)
