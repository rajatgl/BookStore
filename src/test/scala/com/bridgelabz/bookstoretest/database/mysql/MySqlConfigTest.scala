package com.bridgelabz.bookstoretest.database.mysql

import com.bridgelabz.bookstore.database.mysql.configurations.{MySqlConfig, MySqlConnection}
import org.scalatest.flatspec.AnyFlatSpec

class MySqlConfigTest extends AnyFlatSpec{
  MySqlConfig.getConnection(MySqlConnection())
}
