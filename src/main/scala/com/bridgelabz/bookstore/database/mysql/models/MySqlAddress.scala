package com.bridgelabz.bookstore.database.mysql.models

case class MySqlAddress(userId : String,
                        apartmentNumber: String,
                        apartmentName: String,
                        streetAddress: String,
                        landMark: String,
                        state: String,
                        pinCode: String)