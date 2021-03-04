package com.bridgelabz.bookstore.models

/**
 *
 * @param data integer to be sent as verification code
 * @param email associated with the user
 */
case class Otp(data: Int, email: String)
