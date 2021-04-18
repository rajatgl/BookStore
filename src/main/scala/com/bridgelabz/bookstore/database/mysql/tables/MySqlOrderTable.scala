package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.ResultSet

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.MySqlUtils
import com.bridgelabz.bookstore.database.mysql.models.MySqlOrder

import scala.concurrent.Future

class MySqlOrderTable(tableName: String,tableNameForUser : String)
  extends MySqlUtils[MySqlOrder]
    with ICrud[MySqlOrder] {

  val createOrderQuery: String =
    s"""
       |CREATE TABLE IF NOT EXISTS $tableName
       | (userId VARCHAR(50),
       | orderId VARCHAR(50) NOT NULL,
       | transactionId VARCHAR(100),
       | status VARCHAR(100),
       | orderTimestamp LONG,
       | deliveryTimestamp LONG,
       | PRIMARY KEY (orderId),
       | FOREIGN KEY (userId) REFERENCES $tableNameForUser(userId) ON DELETE CASCADE
       | )
       | """.stripMargin

  execute(createOrderQuery)

  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: MySqlOrder): Future[Boolean] = {
    val query: String =
      s"""
         |INSERT INTO $tableName
         |VALUES (
         |  "${entity.orderId}",
         |  "${entity.transactionId}",
         |  "${entity.status}",
         |  "${entity.orderTimestamp}",
         |  "${entity.deliveryTimestamp}"
         |)
         |  """.stripMargin
    try {
      Future.successful(executeUpdate(query) > 0)
    }
    catch {
      case _: Exception => Future.failed(new Exception("Create-MySqlOrder: FAILED"))
    }
  }

  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[MySqlOrder]] = {
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
  override def update(identifier: Any, entity: MySqlOrder, fieldName: String): Future[Boolean] = {
    val query: String =
      s"""
         |UPDATE $tableName SET
         | orderId = "${entity.orderId}",
         | transactionId = "${entity.transactionId}",
         | status = "${entity.status}",
         | orderTimestamp = "${entity.orderTimestamp}",
         | deliveryTimestamp = ${entity.deliveryTimestamp}
         | WHERE $fieldName = "$identifier"
         | """.stripMargin

    if (executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Update-MySqlOrder: FAILED"))
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
      Future.failed(new Exception("Delete-MySqlOrder: FAILED"))
    }
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[MySqlOrder] = {
    var orders = Seq[MySqlOrder]()
    while (resultSet.next()) {
      val order = MySqlOrder(resultSet.getString("userId"),
        resultSet.getString("orderId"),
        resultSet.getString("transactionId"),
        resultSet.getString("status"),
        resultSet.getLong("orderTimestamp"),
        resultSet.getLong("deliveryTimestamp"))
      orders = orders :+ order
    }
    orders
  }
}
