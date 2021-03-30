package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.ResultSet

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.MySqlUtils
import com.bridgelabz.bookstore.database.mysql.models.MySqlCart

import scala.concurrent.Future

class MySqlCartTable(tableName : String,tableNameForUser: String) extends
  MySqlUtils[MySqlCart] with ICrud[MySqlCart]{
  val createCartQuery: String =
    s"""
       |CREATE TABLE IF NOT EXISTS $tableName
       | (cartId VARCHAR(50) NOT NULL,
       | userId VARCHAR(50),
       | PRIMARY KEY (cartId),
       | FOREIGN KEY (userId) REFERENCES $tableNameForUser(userId) ON DELETE CASCADE
       | )
       | """.stripMargin

  execute(createCartQuery)
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
      println("Create cart update count"+executeUpdate(query))
      Future.successful(executeUpdate(query) > 0)
    }
    catch {
      case _: Exception =>
        Future.failed(new Exception("Create-MySqlCart: FAILED"))
    }
  }

  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[MySqlCart]] = {
    val query = s"SELECT * FROM $tableName"
    Future.successful(executeQuery(query))
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

    if (executeUpdate(query) > 0) {
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

    if (executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Delete-MySqlCart: FAILED"))
    }
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[MySqlCart] = {
    var cart = Seq[MySqlCart]()
    while (resultSet.next()) {
      val cartData = MySqlCart(
        resultSet.getString("cartId"),
        resultSet.getString("userId"))
      cart = cart :+ cartData
    }
    cart
  }
}
