package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.ResultSet

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.MySqlUtils
import com.bridgelabz.bookstore.database.mysql.models.MySqlWishList

import scala.concurrent.Future

class MySqlWishListTable(tableName : String,tableNameForUser: String) extends
  MySqlUtils[MySqlWishList] with ICrud[MySqlWishList]{
  val createWishListQuery: String =
  s"""
     |CREATE TABLE IF NOT EXISTS $tableName
     | (userId VARCHAR(50),
     | FOREIGN KEY (userId) REFERENCES $tableNameForUser(userId) ON DELETE CASCADE
     | )
     | """.stripMargin

  execute(createWishListQuery)
  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: MySqlWishList): Future[Boolean] = {
    val query: String =
      s"""
         |INSERT INTO $tableName
         |VALUES (
         |  "${entity.userId}"
         |)
         |  """.stripMargin
    try {
      println("Create cart update count"+executeUpdate(query))
      Future.successful(executeUpdate(query) > 0)
    }
    catch {
      case _: Exception =>
        Future.failed(new Exception("Create-MySqlWishList: FAILED"))
    }
  }

  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[MySqlWishList]] = {
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
  override def update(identifier: Any, entity: MySqlWishList, fieldName: String): Future[Boolean] = {
    val query: String =
      s"""
         |UPDATE $tableName SET
         | userId = "${entity.userId}"
         | WHERE $fieldName = "$identifier"
         | """.stripMargin

    if (executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Update-MySqlWishList: FAILED"))
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
      Future.failed(new Exception("Delete-MySqlWishList: FAILED"))
    }
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[MySqlWishList] = {
    var wishList = Seq[MySqlWishList]()
    while (resultSet.next()) {
      val wishListData = MySqlWishList(
        resultSet.getString("userId"))
      wishList = wishList :+ wishListData
    }
    wishList
  }
}
