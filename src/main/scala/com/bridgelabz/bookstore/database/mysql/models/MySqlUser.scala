package com.bridgelabz.bookstore.database.mysql.models

case class MySqlUser(userId: String,
                     userName: String,
                     mobileNumber: String,
                     email: String,
                     password: String,
                     verificationComplete: Boolean = false)
