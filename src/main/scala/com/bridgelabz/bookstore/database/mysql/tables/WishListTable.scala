package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.{ResultSet, Statement}

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.{MySqlConfig, MySqlConnection, MySqlUtils}
import com.bridgelabz.bookstore.database.mysql.models.{MySqlWishList, MySqlWishListItem}
import com.bridgelabz.bookstore.models.{WishList, WishListItem}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WishListTable(tableName: String,productTableName : String)
  extends MySqlUtils[WishList]
    with ICrud[WishList] {

  val tableNameForWishList: String = tableName.concat("WishList")
  val tableNameForWishListItems: String = tableName.concat("WishListItems")

  val mySqlWishListTable: MySqlWishListTable = new MySqlWishListTable(tableNameForWishList,"usersusers")
  val mySqlWishListItemTable : MySqlWishListItemTable = new MySqlWishListItemTable(tableNameForWishListItems,"usersusers",productTableName)

  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: WishList): Future[Boolean] = {
    try {
      mySqlWishListTable.create(
        MySqlWishList(
          entity.userId
        )
      )
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
    mySqlWishListTable.read().map(mySqlList => {
      var list = Seq[WishList]()
      mySqlList.foreach(mySqlListItem => {
        val items = fetchWishListItems(tableNameForWishListItems, mySqlListItem.userId)
        list = list :+ WishList(
          mySqlListItem.userId,
          items
        )
      })
      list
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
    mySqlWishListTable.update(identifier,
      MySqlWishList(
        entity.userId
      ),
      fieldName
    )
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

    val query: String = s"DELETE FROM $tableNameForWishList WHERE $fieldName = '$identifier'"

    if (executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Delete-WishList-Items: FAILED"))
    }
  }

  /**
   *
   * @param tableNameForWishListItems : Table from where we have to fetch items
   * @param userId: Items for specific cart id
   * @return : Sequence of CartItems
   */
  def fetchWishListItems(tableNameForWishListItems: String, userId: String) : Seq[WishListItem] = {
    val query = s"SELECT * FROM $tableNameForWishListItems WHERE userId = '$userId'"
    var listItems = Seq[WishListItem]()
    val connection = MySqlConfig.getConnection(MySqlConnection())
    try {
      val stmt: Statement = connection.createStatement
      try {
        val rs: ResultSet = stmt.executeQuery(query)
        try {
          while (rs.next()) {
            val item = WishListItem(
              rs.getInt("productId"),
              rs.getLong("timestamp")
            )

            listItems = listItems :+ item
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
    listItems
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[WishList] = Seq()
}