package com.bridgelabz.bookstore.exceptions

import akka.http.javadsl.model.StatusCodes

class ProductQuantityUnavailableException extends IBookStoreException{
  /**
   *
   * @return status code to send as a response
   */
  override def status(): Int = StatusCodes.BAD_REQUEST.intValue()

  /**
   *
   * @return a string representing the exception (to assist with printing/logging)
   */
  override def getMessage: String = ExceptionMessages.messages("PRODUCT_QUANTITY_UNAVAILABLE")
}
