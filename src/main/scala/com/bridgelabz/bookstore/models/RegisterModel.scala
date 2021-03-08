package com.bridgelabz.bookstore.models

/**
 *
 * @param userName of the user
 * @param mobileNumber of the user
 * @param email of the user
 * @param password of the same user account
 */
case class RegisterModel(userName: String,
                         mobileNumber: String,
                         email: String,
                         password: String)
