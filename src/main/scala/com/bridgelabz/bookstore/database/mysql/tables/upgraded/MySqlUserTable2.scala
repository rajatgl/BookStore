package com.bridgelabz.bookstore.database.mysql.tables.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.mysql.configurations.MySqlUtils
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
    Future.successful(executeQuery(query))
  }

  /**
   *
   * @param identifier to identify the item in the database
   * @param entity     which should replace object.parameter
   * @param fieldName  that the identifier belongs to
   * @param parameter  field name that has the value to be updated
   * @return any status regarding the update operation
   */
  override def update[U](identifier: Any, entity: U, fieldName: String, parameter: String): Future[Any] = {

    val query: String =
      s"""
         |UPDATE $tableName SET
         |  $parameter = $entity
         |  WHERE $fieldName = $identifier
         |)
         |  """.stripMargin

    if (executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Update-MySqlUser: FAILED"))
    }
  }
}