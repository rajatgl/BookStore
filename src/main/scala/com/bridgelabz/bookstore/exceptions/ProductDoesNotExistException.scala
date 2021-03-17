package com.bridgelabz.bookstore.exceptions

import akka.http.javadsl.model.StatusCodes

class ProductDoesNotExistException extends IBookStoreException{
  /**
   *
   * @return status code to send as a response
   */
  override def status(): Int = StatusCodes.NOT_FOUND.intValue()

  /**
   *
   * @return a string representing the exception (to assist with printing/logging)
   */
  override def getMessage: String = ExceptionMessages.messages("PRODUCT_DOES_NOT_EXIST")
}