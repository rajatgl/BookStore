package com.bridgelabz.bookstore.database.mysql.configurations

import java.sql.{ResultSet, Statement}

import com.bridgelabz.bookstore.database.mysql.models.{MySqlAddress, MySqlUser}
import com.bridgelabz.bookstore.models.{Address, Product}

/**
 * Created on 3/11/2021.
 * Class: MySqlUtils.scala
 * Author: Rajat G.L.
 */
abstract class MySqlUtils[T] {

  /**
   *
   * @param query to tbe executed [belongs to DDL]
   * @return true/false depending on the status of the operation
   */
  protected def execute(query: String): Boolean = {

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

  /**
   *
   * @param query to tbe executed [belongs to DML]
   * @return int representing the number of rows affected/ updated
   */
  protected def executeUpdate(query: String): Int = {
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

  /**
   *
   * @param query to tbe executed [belongs to DQL]
   * @return Sequence of items- each item representing each row in the MySQL table
   */
  protected def executeQuery(query: String): Seq[T] = {
    var sequence = Seq[T]()
    val connection = MySqlConfig.getConnection(MySqlConnection())
    try {
      val stmt: Statement = connection.createStatement
      try {
        val resultSet: ResultSet = stmt.executeQuery(query)
        try {

          sequence = collectData(resultSet)

        } finally {
          resultSet.close()
        }
      } finally {
        stmt.close()
      }
    } finally {
      connection.close()
    }
    sequence
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  protected def collectData(resultSet: ResultSet): Seq[T]
}