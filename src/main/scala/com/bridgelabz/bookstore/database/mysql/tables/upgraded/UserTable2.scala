package com.bridgelabz.bookstore.database.mysql.tables.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.mysql.MySqlUtils
import com.bridgelabz.bookstore.database.mysql.tables.{MySqlAddressTable, MySqlUserTable, UserTable}
import com.bridgelabz.bookstore.models.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserTable2(tableName: String) extends UserTable(tableName) with ICrudRepository[User] {


  override val mySqlUserTable: MySqlUserTable2 = new MySqlUserTable2(tableNameForUser)
  override val mySqlAddressTable: MySqlAddressTable2 = new MySqlAddressTable2(tableNameForAddress, tableNameForUser)

  /**
   *
   * @param identifier to identify the item in the database
   * @param fieldName  that the identifier belongs to
   * @return a sequence of items that have the identifier
   */
  override def read(identifier: Any, fieldName: String): Future[Seq[User]] = {

    mySqlUserTable.read(identifier, fieldName).map(mySqlUsers => {

      var users = Seq[User]()

      mySqlUsers.foreach(mySqlUser => {

        val addresses = MySqlUtils.fetchAddresses(tableNameForAddress, mySqlUser.userId)

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
}
