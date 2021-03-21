package com.bridgelabz.bookstore.database.mysql

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.models.Product

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created on 3/11/2021.
 * Class: ProductTable.scala
 * Author: Rajat G.L.
 */
class ProductTable(tableName: String) extends ICrud[Product] {

  private def createTable() = {
    val createQuery: String =
      s"""
         |CREATE TABLE IF NOT EXISTS $tableName
         | (productId INT NOT NULL AUTO_INCREMENT,
         | author VARCHAR(50),
         | title VARCHAR(100) UNIQUE,
         | image VARCHAR(1000),
         | quantity INT,
         | price DOUBLE,
         | description TEXT,
         | PRIMARY KEY (productId)
         | )
         | """.stripMargin

    MySqlUtils.execute(createQuery)
  }

  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: Product): Future[Boolean] = {

    createTable()
    val query: String =
      s"""
         |INSERT INTO $tableName(author, title, image, quantity, price, description)
         |VALUES (
         |  "${entity.author}",
         |  "${entity.title}",
         |  "${entity.image}",
         |  ${entity.quantity},
         |  ${entity.price},
         |  "${entity.description}" )""".stripMargin
    try {
        Future.successful(MySqlUtils.executeUpdate(query) > 0)
    }
    catch{
      case exception: Exception => Future.failed(new Exception("Create-Product: FAILED"))
    }
  }

  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[Product]] = {
    val query = s"SELECT * FROM $tableName"
    Future(MySqlUtils.executeProductQuery(query))
  }

  /**
   *
   * @param identifier parameter of the (object in the database to be replaced/updated)
   * @param entity     new object to override the old one
   * @param fieldName  name of the parameter in the object defined by the identifier
   * @return any status identifier for the update operation
   */
  override def update(identifier: Any, entity: Product, fieldName: String): Future[Boolean] = {
    createTable()
    val query: String =
      s"""
         |UPDATE $tableName SET
         | author = "${entity.author}",
         | title = "${entity.title}",
         | image = "${entity.image}",
         | quantity = ${entity.quantity},
         | price = ${entity.price},
         | description = "${entity.description}"
         | WHERE $fieldName = "$identifier"
         | """.stripMargin

    if (MySqlUtils.executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Update-Product: FAILED"))
    }
  }

  /**
   *
   * @param fieldName  name of the parameter in the object defined by the identifier
   * @param identifier parameter of the (object in the database to be deleted)
   * @return any status identifier for the update operation
   */
  override def delete(identifier: Any, fieldName: String): Future[Any] = {
    createTable()
    val query: String =
      s"""
         |DELETE FROM $tableName
         | WHERE $fieldName = "$identifier"
         | """.stripMargin

    if (MySqlUtils.executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Delete-Product: FAILED"))
    }
  }
}
