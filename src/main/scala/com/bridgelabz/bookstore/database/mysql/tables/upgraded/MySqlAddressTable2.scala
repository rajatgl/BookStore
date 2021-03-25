package com.bridgelabz.bookstore.database.mysql.tables.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.mysql.MySqlUtils
import com.bridgelabz.bookstore.database.mysql.models.MySqlAddress
import com.bridgelabz.bookstore.database.mysql.tables.MySqlAddressTable

import scala.concurrent.Future

class MySqlAddressTable2(tableName: String, tableNameForUser: String)
  extends MySqlAddressTable(tableName, tableNameForUser)
    with ICrudRepository[MySqlAddress] {

  /**
   *
   * @param identifier to identify the item in the database
   * @param fieldName  that the identifier belongs to
   * @return a sequence of items that have the identifier
   */
  override def read(identifier: Any, fieldName: String): Future[Seq[MySqlAddress]] = {

    val query = s"SELECT * FROM $tableName WHERE $fieldName = '$identifier'"
    Future.successful(MySqlUtils.executeMySqlAddressQuery(query))
  }
}
