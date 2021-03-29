package com.bridgelabz.bookstore.database.mysql.tables

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.MySqlUtils
import com.bridgelabz.bookstore.database.mysql.models.MySqlCart

import scala.concurrent.Future

class MySqlCartTable(tableName : String,tableNameForUser: String) extends ICrud[MySqlCart]{
  val createCartQuery: String =
    s"""
       |CREATE TABLE IF NOT EXISTS $tableName
       | (cartId VARCHAR(50) NOT NULL,
       | userId VARCHAR(50),
       | PRIMARY KEY (cartId)
       | FOREIGN KEY (userId) REFERENCES $tableNameForUser(userId) ON DELETE CASCADE
       | )
       | """.stripMargin

  MySqlUtils.execute(createCartQuery)
  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: MySqlCart): Future[Boolean] = {
    val query: String =
      s"""
         |INSERT INTO $tableName
         |VALUES (
         |  "${entity.cartId}",
         |  "${entity.userId}"
         |)
         |  """.stripMargin
    try {
      Future.successful(MySqlUtils.executeUpdate(query) > 0)
    }
    catch {
      case _: Exception => Future.failed(new Exception("Create-MySqlCart: FAILED"))
    }
  }

  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[MySqlCart]] = {
    val query = s"SELECT * FROM $tableName"
    Future.successful(MySqlUtils.executeMySqlCartQuery(query))
  }

  /**
   *
   * @param identifier parameter of the (object in the database to be replaced/updated)
   * @param entity     new object to override the old one
   * @param fieldName  name of the parameter in the object defined by the identifier
   * @return any status identifier for the update operation
   */
  override def update(identifier: Any, entity: MySqlCart, fieldName: String): Future[Boolean] = {
    val query: String =
      s"""
         |UPDATE $tableName SET
         | cartId = "${entity.cartId}",
         | userId = "${entity.userId}"
         | WHERE $fieldName = "$identifier"
         | """.stripMargin

    if (MySqlUtils.executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Update-MySqlCart: FAILED"))
    }
  }

  /**
   *
   * @param fieldName  name of the parameter in the object defined by the identifier
   * @param identifier parameter of the (object in the database to be deleted)
   * @return any status identifier for the update operation
   */
  override def delete(identifier: Any, fieldName: String): Future[Boolean] = {
    val query: String =
      s"""
         |DELETE FROM $tableName
         | WHERE $fieldName = "$identifier"
         | """.stripMargin

    if (MySqlUtils.executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Delete-MySqlUser: FAILED"))
    }
  }
}
