package com.bridgelabz.bookstore.models

/**
 *
 * @param email representing User ID
 * @param password to login to the account
 * @param verificationComplete if user verified or not
 */
case class User(email: String, password: String, verificationComplete: Boolean = false)
