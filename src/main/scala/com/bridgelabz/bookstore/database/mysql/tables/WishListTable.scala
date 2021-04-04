package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.{ResultSet, Statement}

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.{MySqlConfig, MySqlConnection, MySqlUtils}
import com.bridgelabz.bookstore.database.mysql.models.MySqlWishListItem
import com.bridgelabz.bookstore.models.{WishList, WishListItem}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WishListTable(tableName: String, productTableName : String, userTableName: String)
  extends MySqlUtils[WishList]
    with ICrud[WishList] {

  val mySqlWishListItemTable : MySqlWishListItemTable = new MySqlWishListItemTable(tableName, userTableName, productTableName)

  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: WishList): Future[Boolean] = {
    try {

      for(item <- entity.items){
        mySqlWishListItemTable.create(
          MySqlWishListItem(
            entity.userId,
            item.productId,
            item.timestamp
          )
        )
      }
      Future.successful(true)
    }
    catch {
      case _: Exception => Future.failed(new Exception("Create-WishList-Item: FAILED"))
    }
  }


  /**
   *
   * @return sequence of objects in the database
   */
  override def read(): Future[Seq[WishList]] = {
    mySqlWishListItemTable.read().map(seq => {

      var finalSeq: Seq[WishList] = Seq()
      val sortedSeq: Map[String, Seq[MySqlWishListItem]] = seq.groupBy(item => item.userId)

      for((key, value) <- sortedSeq){
        var cartItems: Seq[WishListItem] = Seq()
        for(item <- value) {
          cartItems = cartItems :+ WishListItem(item.productId, item.timestamp)
        }
        finalSeq = finalSeq :+ WishList(key, cartItems)
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
  override def update(identifier: Any, entity: WishList, fieldName: String): Future[Boolean] = {
    mySqlWishListItemTable.delete(entity.userId,"userId")
    for(item <- entity.items){
      mySqlWishListItemTable.create(
        MySqlWishListItem(
          entity.userId,
          item.productId,
          item.timestamp
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
      Future.failed(new Exception("Delete-WishList-Items: FAILED"))
    }
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[WishList] = Seq()
}
