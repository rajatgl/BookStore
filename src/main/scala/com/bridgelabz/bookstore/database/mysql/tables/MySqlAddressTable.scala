package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.ResultSet

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.MySqlUtils
import com.bridgelabz.bookstore.database.mysql.models.MySqlAddress

import scala.concurrent.Future

protected class MySqlAddressTable(tableName: String, tableNameForUser: String)
  extends MySqlUtils[MySqlAddress] with ICrud[MySqlAddress] {

  val createAddressQuery: String =
    s"""
       |CREATE TABLE IF NOT EXISTS $tableName
       | (
       | userId VARCHAR(50) NOT NULL,
       | apartmentNumber VARCHAR(20),
       | apartmentName VARCHAR(40),
       | streetAddress VARCHAR(20),
       | landMark VARCHAR(30),
       | state VARCHAR(30),
       | pinCode VARCHAR(6),
       | FOREIGN KEY (userId) REFERENCES $tableNameForUser(userId) ON DELETE CASCADE
       | )
       | """.stripMargin

  execute(createAddressQuery)

  /**
   *
   * @param address object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(address: MySqlAddress): Future[Boolean] = {

    val query: String =
      s"""
         |INSERT INTO $tableName(userId, apartmentNumber, apartmentName, streetAddress, landmark, state, pincode)
         |VALUES (
         |  "${address.userId}",
         |  "${address.apartmentNumber}",
         |  "${address.apartmentName}",
         |  "${address.streetAddress}",
         |  "${address.landMark}",
         |  "${address.state}",
         |  "${address.pinCode}"
         |)
         |  """.stripMargin

    if (executeUpdate(query) > 1) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Create-Address: FAILED"))
    }
  }

  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[MySqlAddress]] = {
    val query = s"SELECT * FROM $tableName"
    Future.successful(executeQuery(query))
  }

  /**
   *
   * @param identifier parameter of the (object in the database to be replaced/updated)
   * @param address     new object to override the old one
   * @param fieldName  name of the parameter in the object defined by the identifier
   * @return any status identifier for the update operation
   */
  override def update(identifier: Any, address: MySqlAddress, fieldName: String): Future[Boolean] = {

    val query: String =
      s"""
         |UPDATE $tableName SET
         |  apartmentNumber = "${address.apartmentNumber}",
         |  apartmentName = "${address.apartmentName}",
         |  streetAddress = "${address.streetAddress}",
         |  landMark = "${address.landMark}",
         |  state = "${address.state}",
         |  pinCode = "${address.pinCode}"
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
      Future.failed(new Exception("Delete-MySqlUser: FAILED"))
    }
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[MySqlAddress] = {

    var addresses = Seq[MySqlAddress]()

    while (resultSet.next()) {

      val address = MySqlAddress(
        resultSet.getString("userId"),
        resultSet.getInt("apartmentNumber").toString,
        resultSet.getString("apartmentName"),
        resultSet.getString("streetAddress"),
        resultSet.getString("landMark"),
        resultSet.getString("state"),
        resultSet.getString("pincode"))

      addresses = addresses :+ address
    }

    addresses
  }
}
