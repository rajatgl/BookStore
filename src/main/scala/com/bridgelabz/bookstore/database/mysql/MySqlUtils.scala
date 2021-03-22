package com.bridgelabz.bookstore.database.mysql

import java.sql.{ResultSet, Statement}

import com.bridgelabz.bookstore.models.Product

/**
 * Created on 3/11/2021.
 * Class: MySqlUtils.scala
 * Author: Rajat G.L.
 */
object MySqlUtils {

  def execute(query: String): Boolean = {

    var successful: Boolean = false
    val connection = MySqlConfig.getConnection(MySqlConnection())
    try {
      val stmt: Statement = connection.createStatement
      try {
        successful = stmt.execute(query)
      } finally {
        stmt.close()
      }
    } finally {
      connection.close()
    }

    successful
  }

  def executeUpdate(query: String): Int = {

    var successful: Int = 0
    val connection = MySqlConfig.getConnection(MySqlConnection())
    try {
      val stmt: Statement = connection.createStatement
      try {
        successful = stmt.executeUpdate(query)
      } finally {
        stmt.close()
      }
    } finally {
      connection.close()
    }

    successful
  }

  def executeQuery(query: String): Seq[Product]= {
    var products = Seq[Product]()
    val connection = MySqlConfig.getConnection(MySqlConnection())
    try {
      val stmt: Statement = connection.createStatement
      try {
        val rs: ResultSet  = stmt.executeQuery(query)
        try {
          while (rs.next()) {
            val product = Product(rs.getString("productId"),
              rs.getString("author"),
              rs.getString("title"),
              rs.getString("image"),
              rs.getString("quantity"),
              rs.getString("price"),
              rs.getString("description"))
            products = products :+ product
          }
        } finally {
          rs.close()
        }
      } finally {
        stmt.close()
      }
    } finally {
      connection.close()
    }
    products
  }

}
