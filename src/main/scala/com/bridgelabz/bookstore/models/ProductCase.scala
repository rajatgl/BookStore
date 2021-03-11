package com.bridgelabz.bookstore.models

/**
 *
 * @param Pid : Id of product
 * @param author : Author of the book
 * @param title : Title of the book
 * @param quantity : Quantity of the product
 * @param price : Price of the book
 * @param description : Short description about the book
 */
case class ProductCase(Pid: Int, author: String, title: String, quantity: Int, price: Int, description: String)
