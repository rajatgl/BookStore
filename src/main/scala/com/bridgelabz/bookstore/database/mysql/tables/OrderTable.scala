package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.ResultSet

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.MySqlUtils
import com.bridgelabz.bookstore.database.mysql.models.{MySqlAddress, MySqlCartItem, MySqlOrder}
import com.bridgelabz.bookstore.models.{Address, CartItem, Order}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrderTable(tableName: String,productTableName : String, userTableName: String)
  extends MySqlUtils[Order]
    with ICrud[Order] {

  val tableNameForOrder: String = tableName.concat("Orders")

  val mySqlOrderTable : MySqlOrderTable = new MySqlOrderTable(tableNameForOrder,userTableName)
  val mySqlCartItemTable : MySqlCartItemTable = new MySqlCartItemTable(tableName, userTableName, productTableName)
  val mySqlAddressTable : MySqlAddressTable = new MySqlAddressTable("usersaddresses",userTableName)

  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: Order): Future[Boolean] = {
    try {
      mySqlOrderTable.create(
        MySqlOrder(
          entity.userId,
          entity.orderId,
          entity.transactionId,
          entity.status,
          entity.orderTimestamp,
          entity.deliveryTimestamp
        )
      )
        mySqlAddressTable.create(
          MySqlAddress(
            entity.userId,
            entity.deliveryAddress.apartmentName,
            entity.deliveryAddress.apartmentNumber,
            entity.deliveryAddress.streetAddress,
            entity.deliveryAddress.landMark,
            entity.deliveryAddress.state,
            entity.deliveryAddress.pinCode)
        )

      for(item <- entity.items){
        mySqlCartItemTable.create(
          MySqlCartItem(
            entity.userId,
            item.timestamp,
            item.productId,
            item.quantity
          )
        )
      }
      Future.successful(true)
    }
    catch {
      case _: Exception => Future.failed(new Exception("Create-Order: FAILED"))
    }
  }


  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[Order]] = {
    val elements = for {
      orders <- mySqlOrderTable.read()
      addresses <- mySqlAddressTable.read()
      items <- mySqlCartItemTable.read()
    } yield (orders, addresses,items)

    elements.map(tuple => {

      var orders = Seq[Order]()
      tuple._1.foreach(mySqlOrder => {

        val addresses = tuple._2.filter(address => address.userId.equals(mySqlOrder.userId)).map(mySqlAddress => {
          Address(mySqlAddress.apartmentNumber,
            mySqlAddress.apartmentName,
            mySqlAddress.streetAddress,
            mySqlAddress.landMark,
            mySqlAddress.state,
            mySqlAddress.pinCode
          )
        })


        val items = tuple._3.filter(item => item.userId.equals(mySqlOrder.userId)).map(mySqlItem => {
          CartItem(mySqlItem.productId,
            mySqlItem.timestamp,
            mySqlItem.quantity
          )
        })

        orders = orders :+ Order(
          mySqlOrder.userId,
          mySqlOrder.orderId,
          mySqlOrder.transactionId,
          addresses.asInstanceOf[Address],
          items,
          mySqlOrder.status,
          mySqlOrder.orderTimestamp,
          mySqlOrder.deliveryTimestamp
        )
      })
      orders
    })
  }

  /**
   *
   * @param identifier parameter of the (object in the database to be replaced/updated)
   * @param entity     new object to override the old one
   * @param fieldName  name of the parameter in the object defined by the identifier
   * @return any status identifier for the update operation
   */
  override def update(identifier: Any, entity: Order, fieldName: String): Future[Boolean] = {
    mySqlOrderTable.update(identifier,
      MySqlOrder(
        entity.userId,
        entity.orderId,
        entity.transactionId,
        entity.status,
        entity.orderTimestamp,
        entity.deliveryTimestamp
      ),
      fieldName
    )
    mySqlAddressTable.delete(entity.userId, "userId")
    mySqlCartItemTable.delete(entity.userId,"userId")

    mySqlAddressTable.create(
      MySqlAddress(
        entity.userId,
        entity.deliveryAddress.apartmentName,
        entity.deliveryAddress.apartmentNumber,
        entity.deliveryAddress.streetAddress,
        entity.deliveryAddress.landMark,
        entity.deliveryAddress.state,
        entity.deliveryAddress.pinCode)
    )

    for(item <- entity.items){
      mySqlCartItemTable.create(
        MySqlCartItem(
          entity.userId,
          item.timestamp,
          item.productId,
          item.quantity
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

    val query: String = s"DELETE FROM $tableNameForOrder WHERE $fieldName = '$identifier'"

    if (executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Delete-Order: FAILED"))
    }
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[Order] = Seq()
}