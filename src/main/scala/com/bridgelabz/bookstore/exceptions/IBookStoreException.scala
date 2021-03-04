package com.bridgelabz.bookstore.exceptions

trait IBookStoreException extends Exception {

  /**
   *
   * @return status code to send as a response
   */
  def status(): Int

  /**
   *
   * @return a string representing the exception (to assist with printing/logging)
   */
  def getMessage: String
}
