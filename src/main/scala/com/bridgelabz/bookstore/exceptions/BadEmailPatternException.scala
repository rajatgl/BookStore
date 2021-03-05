package com.bridgelabz.bookstore.exceptions

import akka.http.javadsl.model.StatusCodes

class BadEmailPatternException() extends IBookStoreException {

  /**
   *
   * @return a string representing the exception (to assist with printing/logging)
   */
  override def getMessage: String = "BAD_EMAIL: Please enter a valid email address."

  /**
   *
   * @return status code to send as a response
   */
  override def status(): Int = StatusCodes.BAD_REQUEST.intValue()
}
