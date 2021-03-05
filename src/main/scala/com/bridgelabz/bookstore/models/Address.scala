package com.bridgelabz.bookstore.models

/**
 *
 * @param apartmentNumber associated with the suite
 * @param apartmentName name of the apartment
 * @param streetAddress of the apartment
 * @param landMark nearby landmark
 * @param state associated with the residence
 * @param pinCode associated with the residence
 */
case class Address(apartmentNumber: String,
                   apartmentName: String,
                   streetAddress: String,
                   landMark: String,
                   state: String,
                   pinCode: String)
