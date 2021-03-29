package com.bridgelabz.bookstore.database.mysql.tables

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.models.MySqlUser

import scala.concurrent.Future

protected class CartTable(tableName: String) extends ICrud[MySqlUser] {

  val createUserQuery: String =
    s"""
       |CREATE TABLE IF NOT EXISTS $tableName
       | (cartId VARCHAR(50) NOT NULL,
       | userId VARCHAR(50),
       | cartItem VARCHAR(20),
       | PRIMARY KEY (cartId)
       | )
       | """.stripMargin

  MySqlUtils.execute(createUserQuery)

  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: MySqlUser): Future[Boolean] = {

    val query: String =
      s"""
         |INSERT INTO $tableName
         |VALUES (

         |)
         |  """.stripMargin
    try {
      Future.successful(MySqlUtils.executeUpdate(query) > 0)
    }
    catch {
      case exception: Exception => Future.failed(new Exception("Create-MySqlUser: FAILED"))
    }
  }

  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[MySqlUser]] = {
    val query = s"SELECT * FROM $tableName"
    Future.successful(MySqlUtils.executeMySqlUserQuery(query))
  }

  /**
   *
   * @param identifier parameter of the (object in the database to be replaced/updated)
   * @param entity     new object to override the old one
   * @param fieldName  name of the parameter in the object defined by the identifier
   * @return any status identifier for the update operation
   */
  override def update(identifier: Any, entity: MySqlUser, fieldName: String): Future[Boolean] = {

    val query: String =
      s"""
         |UPDATE $tableName SET

         | """.stripMargin

    if (MySqlUtils.executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Update-MySqlUser: FAILED"))
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
