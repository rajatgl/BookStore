package com.bridgelabz.bookstore.exceptions

trait IBookStoreException extends Exception {

  def status(): Int
  def getMessage: String
}
