package com.bridgelabz.bookstore.database.mysql

import java.sql.{ResultSet, Statement}

import com.bridgelabz.bookstore.database.mysql.models.{MySqlAddress, MySqlUser}
import com.bridgelabz.bookstore.models.{Address, Product}

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

  def executeProductQuery(query: String): Seq[Product]= {
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

  def executeMySqlUserQuery(query: String): Seq[MySqlUser] = {
    var users = Seq[MySqlUser]()
    val connection = MySqlConfig.getConnection(MySqlConnection())
    try {
      val stmt: Statement = connection.createStatement
      try {
        val rs: ResultSet  = stmt.executeQuery(query)
        try {
          while (rs.next()) {
            val user = MySqlUser(rs.getString("userId"),
              rs.getString("userName"),
              rs.getString("mobileNumber"),
              rs.getString("email"),
              rs.getString("password"),
              rs.getBoolean("verificationComplete"))
            users = users :+ user
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
    users
  }

  def executeMySqlAddressQuery(query: String): Seq[MySqlAddress] = {
    var addresses = Seq[MySqlAddress]()
    val connection = MySqlConfig.getConnection(MySqlConnection())
    try {
      val stmt: Statement = connection.createStatement
      try {
        val rs: ResultSet  = stmt.executeQuery(query)
        try {
          while (rs.next()) {
            val address = MySqlAddress(
              rs.getString("userId"),
              rs.getInt("apartmentNumber").toString,
              rs.getString("apartmentName"),
              rs.getString("streetAddress"),
              rs.getString("landMark"),
              rs.getString("state"),
              rs.getString("pincode"))
            addresses = addresses :+ address
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
    addresses
  }

  def fetchAddresses(tableNameForAddress: String, userId: String): Seq[Address] = {

    val query = s"SELECT * FROM $tableNameForAddress WHERE userId = $userId"
    var addresses = Seq[Address]()
    val connection = MySqlConfig.getConnection(MySqlConnection())
    try {
      val stmt: Statement = connection.createStatement
      try {
        val rs: ResultSet  = stmt.executeQuery(query)
        try {
          while (rs.next()) {
            val address = Address(rs.getInt("apartmentNumber").toString,
              rs.getString("apartmentName"),
              rs.getString("streetAddress"),
              rs.getString("landMark"),
              rs.getString("state"),
              rs.getString("pincode"))
            addresses = addresses :+ address
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
    addresses
  }
}
