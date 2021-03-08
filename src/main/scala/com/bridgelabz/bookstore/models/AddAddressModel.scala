package com.bridgelabz.bookstore.models

/**
 *
 * @param userId of the concerned user
 * @param address the address object to be added to the user's account
 */
case class AddAddressModel(userId: String, address: Address)
