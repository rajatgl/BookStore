package com.bridgelabz.bookstore.exceptions

import akka.http.javadsl.model.StatusCodes

class PasswordMismatchException extends IBookStoreException {

  /**
   *
   * @return a string representing the exception (to assist with printing/logging)
   */
  override def getMessage: String = "PASSWORD_MISMATCH: Please enter a valid password for given email."

  /**
   *
   * @return status code to send as a response
   */
  override def status(): Int = StatusCodes.UNAUTHORIZED.intValue()
}
