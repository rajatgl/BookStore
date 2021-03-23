package com.bridgelabz.bookstore.database.mysql.tables

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.mysql.MySqlUtils
import com.bridgelabz.bookstore.models.Product

import scala.concurrent.Future

class ProductTable2(tableName: String) extends ProductTable(tableName) with ICrudRepository[Product] {

  override def readByValue(identifier: Any, fieldName: String): Future[Seq[Product]] = {

    val query = s"SELECT * FROM $tableName WHERE $fieldName = '$identifier'"
    Future.successful(MySqlUtils.executeProductQuery(query))
  }
}
