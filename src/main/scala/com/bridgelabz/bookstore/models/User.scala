package com.bridgelabz.bookstore.models

/**
 *
 * @param userId an identifier for the user
 * @param userName provided by the user
 * @param mobileNumber provided by the user
 * @param addresses delivery addresses provided by the user
 * @param email provided by the user
 * @param password to login to the account
 * @param verificationComplete if user verified or not
 */
case class User(userId: String,
                userName: String,
                mobileNumber: String,
                addresses: Seq[Address],
                email: String,
                password: String,
                verificationComplete: Boolean = false)
