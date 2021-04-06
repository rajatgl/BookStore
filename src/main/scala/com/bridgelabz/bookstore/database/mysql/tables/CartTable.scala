package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.{ResultSet, Statement}

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.{MySqlConfig, MySqlConnection, MySqlUtils}
import com.bridgelabz.bookstore.database.mysql.models.{MySqlCart, MySqlCartItem}
import com.bridgelabz.bookstore.models.{Cart, CartItem}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CartTable(tableName: String,productTableName : String, userTableName: String)
  extends MySqlUtils[Cart]
    with ICrud[Cart] {

  val mySqlCartItemTable : MySqlCartItemTable = new MySqlCartItemTable(tableName, userTableName, productTableName)

  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: Cart): Future[Boolean] = {
    try {

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
      case _: Exception => Future.failed(new Exception("Create-Cart-Item: FAILED"))
    }
  }


  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[Cart]] = {

    mySqlCartItemTable.read().map(seq => {

      var finalSeq: Seq[Cart] = Seq()
      val sortedSeq: Map[String, Seq[MySqlCartItem]] = seq.groupBy(item => item.userId)

      for((key, value) <- sortedSeq){
        var cartItems: Seq[CartItem] = Seq()
        for(item <- value) {
          cartItems = cartItems :+ CartItem(item.productId, item.timestamp, item.quantity)
        }
        finalSeq = finalSeq :+ Cart(key, cartItems)
      }

      finalSeq
    })
  }

  /**
   *
   * @param identifier parameter of the (object in the database to be replaced/updated)
   * @param entity     new object to override the old one
   * @param fieldName  name of the parameter in the object defined by the identifier
   * @return any status identifier for the update operation
   */
  override def update(identifier: Any, entity: Cart, fieldName: String): Future[Boolean] = {

    mySqlCartItemTable.delete(entity.userId,"userId")
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

    val query: String = s"DELETE FROM $tableName WHERE $fieldName = '$identifier'"

    if (executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Delete-Cart-Items: FAILED"))
    }
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[Cart] = Seq()
}