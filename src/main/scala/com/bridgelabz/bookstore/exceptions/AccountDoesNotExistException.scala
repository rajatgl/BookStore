package com.bridgelabz.bookstore.exceptions

import akka.http.javadsl.model.StatusCodes

class AccountDoesNotExistException extends IBookStoreException {
  /**
   *
   * @return status code to send as a response
   */
  override def status(): Int = StatusCodes.UNAUTHORIZED.intValue()

  /**
   *
   * @return a string representing the exception (to assist with printing/logging)
   */
  override def getMessage: String = "ACCOUNT_DOES_NOT_EXIST: You are not authorized to access this content."
}
