package com.bridgelabz.bookstore.database.mysql.tables.upgraded

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.mysql.models.MySqlAddress
import com.bridgelabz.bookstore.database.mysql.tables.UserTable
import com.bridgelabz.bookstore.models.{Address, User}

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

        val addresses = mySqlAddressTable.fetch(mySqlUser.userId, "userId").map(mySqlAddress => {
          Address(mySqlAddress.apartmentNumber,
            mySqlAddress.apartmentName,
            mySqlAddress.streetAddress,
            mySqlAddress.landMark,
            mySqlAddress.state,
            mySqlAddress.pinCode
          )
        })

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
   * @param identifier to identify the item in the database
   * @param entity     which should replace object.parameter
   * @param fieldName  that the identifier belongs to
   * @param parameter  field name that has the value to be updated
   * @return any status regarding the update operation
   */
  override def update[U](identifier: Any, entity: U, fieldName: String, parameter: String): Future[Any] = {
    if(parameter.toLowerCase.equals("addresses")){
      if(fieldName == "userId") {
        mySqlAddressTable.delete(identifier, "userId")
        for (address <- entity.asInstanceOf[Seq[Address]]) {
          mySqlAddressTable.create(
            MySqlAddress(
              identifier.asInstanceOf,
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
      else{
        mySqlAddressTable.update(identifier, entity, fieldName, parameter)
      }
    }
    else{
      mySqlUserTable.update(identifier, entity, fieldName, parameter)
    }
  }
}
