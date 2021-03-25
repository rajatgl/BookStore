package com.bridgelabz.bookstore.database.mysql.tables.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.mysql.MySqlUtils
import com.bridgelabz.bookstore.database.mysql.models.MySqlUser
import com.bridgelabz.bookstore.database.mysql.tables.MySqlUserTable

import scala.concurrent.Future

class MySqlUserTable2(tableName: String) extends MySqlUserTable(tableName) with ICrudRepository[MySqlUser] {
  /**
   *
   * @param identifier to identify the item in the database
   * @param fieldName  that the identifier belongs to
   * @return a sequence of items that have the identifier
   */
  override def read(identifier: Any, fieldName: String): Future[Seq[MySqlUser]] = {
    val query = s"SELECT * FROM $tableName WHERE $fieldName = '$identifier'"
    Future.successful(MySqlUtils.executeMySqlUserQuery(query))
  }
}
