package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.{ResultSet, Statement}

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.{MySqlConfig, MySqlConnection, MySqlUtils}
import com.bridgelabz.bookstore.database.mysql.models.{MySqlAddress, MySqlUser}
import com.bridgelabz.bookstore.models.{Address, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserTable(tableName: String)
  extends MySqlUtils[User]
    with ICrud[User] {

  val tableNameForAddress: String = tableName.concat("Addresses")
  val tableNameForUser: String = tableName.concat("Users")

  val mySqlUserTable: MySqlUserTable = new MySqlUserTable(tableNameForUser)
  val mySqlAddressTable: MySqlAddressTable = new MySqlAddressTable(tableNameForAddress, tableNameForUser)

  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: User): Future[Boolean] = {

    try {

      mySqlUserTable.create(
        MySqlUser(
          entity.userId,
          entity.userName,
          entity.mobileNumber,
          entity.email,
          entity.password,
          entity.verificationComplete
        )
      )

      for (address <- entity.addresses) {

        mySqlAddressTable.create(
          MySqlAddress(
            entity.userId,
            address.apartmentNumber,
            address.apartmentName,
            address.streetAddress,
            address.landMark,
            address.state,
            address.pinCode
          )
        )
      }

      Future.successful(true)
    }
    catch {
      case exception: Exception => Future.failed(new Exception("Create-User-Address: FAILED"))
    }
  }

  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[User]] = {
    mySqlUserTable.read().map(mySqlUsers => {

      var users = Seq[User]()

      mySqlUsers.foreach(mySqlUser => {

        val addresses = fetchAddresses(tableNameForAddress, mySqlUser.userId)

        users = users :+ User(
          mySqlUser.userId,
          mySqlUser.userName,
          mySqlUser.mobileNumber,
          addresses,
          mySqlUser.email,
          mySqlUser.password,
          mySqlUser.verificationComplete
        )
      })

      users
    })
  }

  /**
   *
   * @param identifier parameter of the (object in the database to be replaced/updated)
   * @param entity     new object to override the old one
   * @param fieldName  name of the parameter in the object defined by the identifier
   * @return any status identifier for the update operation
   */
  override def update(identifier: Any, entity: User, fieldName: String): Future[Boolean] = {

    mySqlUserTable.update(identifier,
      MySqlUser(
        entity.userId,
        entity.userName,
        entity.mobileNumber,
        entity.email,
        entity.password,
        entity.verificationComplete
      ),
      fieldName
    )

    mySqlAddressTable.delete(entity.userId, "userId")
    for (address <- entity.addresses) {
      mySqlAddressTable.create(
        MySqlAddress(
          entity.userId,
          address.apartmentNumber,
          address.apartmentName,
          address.streetAddress,
          address.landMark,
          address.state,
          address.pinCode
        )
      )
    }

    Future.successful(true)

  }

  /**
   *
   * @param fieldName  name of the parameter in the object defined by the identifier
   * @param identifier parameter of the (object in the database to be deleted)
   * @return any status identifier for the update operation
   */
  override def delete(identifier: Any, fieldName: String): Future[Boolean] = {

    val query: String = s"DELETE FROM $tableNameForUser WHERE $fieldName = '$identifier'"

    if (executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Delete-User-Address: FAILED"))
    }
  }

  def fetchAddresses(tableNameForAddress: String, userId: String): Seq[Address] = {

    val query = s"SELECT * FROM $tableNameForAddress WHERE userId = '$userId'"
    var addresses = Seq[Address]()
    val connection = MySqlConfig.getConnection(MySqlConnection())
    try {
      val stmt: Statement = connection.createStatement
      try {
        val rs: ResultSet = stmt.executeQuery(query)
        try {
          while (rs.next()) {
            val address = Address(rs.getInt("apartmentNumber").toString,
              rs.getString("apartmentName"),
              rs.getString("streetAddress"),
              rs.getString("landMark"),
              rs.getString("state"),
              rs.getString("pincode"))

            addresses = addresses :+ address
          }
        } finally {
          rs.close()
        }
      } finally {
        stmt.close()
      }
    } finally {
      connection.close()
    }
    addresses

  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[User] = Seq()
}
