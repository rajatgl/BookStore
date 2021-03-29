package com.bridgelabz.bookstore.database.mysql.configurations

import java.sql.{Connection, DriverManager}

import com.typesafe.scalalogging.Logger

/**
 * Created on 3/28/2021.
 * Class: MySqlConfig.scala
 * Author: Rajat G.L.
 */
object MySqlConfig {

  private var driverLoaded = false
  val logger: Logger = Logger("MySql Config")

  private def loadDriver() {
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance
      driverLoaded = true
    } catch {
      case e: Exception =>
        logger.error("Driver not available: " + e.getMessage)
        throw e
    }
  }

  def getConnection(dbc: MySqlConnection): Connection = {
    // Only load driver first time
    this.synchronized {
      if (!driverLoaded) loadDriver()
    }

    // Get the connection
    try {
      DriverManager.getConnection(dbc.getConnectionString)
    } catch {
      case e: Exception =>
        logger.error("No connection: " + e.getMessage)
        throw e
    }
  }

}
