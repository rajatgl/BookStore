package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.ResultSet

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.MySqlUtils
import com.bridgelabz.bookstore.database.mysql.models.MySqlCartItem

import scala.concurrent.Future

class MySqlCartItemTable(tableName : String,tableNameForUser: String,tableNameForProduct: String)
  extends MySqlUtils[MySqlCartItem]
    with ICrud[MySqlCartItem] {
  val createCartItemQuery: String =
    s"""
       |CREATE TABLE IF NOT EXISTS $tableName
       | (userId VARCHAR(50),
       | productId INT,
       | timestamp LONG,
       | quantity INT,
       | FOREIGN KEY (userId) REFERENCES $tableNameForUser(userId) ON DELETE CASCADE
       | )
       | """.stripMargin

  execute(createCartItemQuery)
  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: MySqlCartItem): Future[Boolean] = {
    val query: String =
      s"""
         |INSERT INTO $tableName
         |VALUES (
         |  "${entity.userId}",
         |  ${entity.productId},
         |  ${entity.timestamp},
         |  ${entity.quantity}
         |)
         |  """.stripMargin
    try {
      println("Create Item update count"+executeUpdate(query))
      Future.successful(executeUpdate(query) > 0)
    }
    catch {
      case _: Exception => Future.failed(new Exception("Create-MySqlCartItem: FAILED"))
    }
  }

  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[MySqlCartItem]] = {
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
  override def update(identifier: Any, entity: MySqlCartItem, fieldName: String): Future[Boolean] = {
    val query: String =
      s"""
         |UPDATE $tableName SET
         | cartId = "${entity.userId}",
         | productId = ${entity.productId},
         | timestamp = ${entity.timestamp}
         | quantity = ${entity.quantity}
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
      Future.failed(new Exception("Delete-MySqlCartItem: FAILED"))
    }
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[MySqlCartItem] = {
    var cartItems = Seq[MySqlCartItem]()
    while (resultSet.next()) {
      val cartItem = MySqlCartItem(
        resultSet.getString("userId"),
        resultSet.getLong("timestamp"),
        resultSet.getInt("productId"),
        resultSet.getInt("quantity"))
      cartItems = cartItems :+ cartItem
    }
    cartItems
  }
}