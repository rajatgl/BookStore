package com.bridgelabz.bookstore.exceptions

import akka.http.javadsl.model.StatusCodes

class BadEmailPattern() extends IBookStoreException {

  override def getMessage: String = "BAD_EMAIL: Please enter a valid email address."
  override def status(): Int = StatusCodes.BAD_REQUEST.intValue()
}
