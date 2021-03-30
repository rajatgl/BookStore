package com.bridgelabz.bookstore.database.mysql.tables

import java.sql.{ResultSet, Statement}

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.configurations.{MySqlConfig, MySqlConnection, MySqlUtils}
import com.bridgelabz.bookstore.database.mysql.models.{MySqlCart, MySqlCartItem}
import com.bridgelabz.bookstore.models.{Cart, CartItem}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CartTable(tableName: String,productTableName : String)
  extends MySqlUtils[Cart]
  with ICrud[Cart] {

  val tableNameForCart: String = tableName.concat("Cart")
  val tableNameForCartItems: String = tableName.concat("CartItems")

  val mySqlCartTable: MySqlCartTable = new MySqlCartTable(tableNameForCart,"usersusers")
  val mySqlCartItemTable : MySqlCartItemTable = new MySqlCartItemTable(tableNameForCartItems,tableNameForCart,productTableName)

  /**
   *
   * @param entity object to be created in the database
   * @return any status identifier for the create operation
   */
  override def create(entity: Cart): Future[Boolean] = {
    try {
      mySqlCartTable.create(
        MySqlCart(
          entity.cartId,
          entity.userId
        )
      )
      for(item <- entity.items){
        mySqlCartItemTable.create(
          MySqlCartItem(
            entity.cartId,
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
    mySqlCartTable.read().map(mySqlCart => {
      var cart = Seq[Cart]()
      mySqlCart.foreach(mySqlCartItem => {
        val items = fetchCartItems(tableNameForCartItems, mySqlCartItem.cartId)
        cart = cart :+ Cart(
          mySqlCartItem.cartId,
          mySqlCartItem.userId,
          items
        )
      })
      cart
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
    mySqlCartTable.update(identifier,
      MySqlCart(
        entity.cartId,
        entity.userId
      ),
      fieldName
    )
    mySqlCartItemTable.delete(entity.cartId,"cartId")
    for(item <- entity.items){
      mySqlCartItemTable.create(
        MySqlCartItem(
          entity.cartId,
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

    val query: String = s"DELETE FROM $tableNameForCart WHERE $fieldName = '$identifier'"

    if (executeUpdate(query) > 0) {
      Future.successful(true)
    }
    else {
      Future.failed(new Exception("Delete-Cart-Items: FAILED"))
    }
  }

  /**
   *
   * @param tableNameForCartItems : Table from where we have to fetch items
   * @param cartId : Items for specific cart id
   * @return : Sequence of CartItems
   */
  def fetchCartItems(tableNameForCartItems: String, cartId: String) : Seq[CartItem] = {
    val query = s"SELECT * FROM $tableNameForCartItems WHERE cartId = '$cartId'"
    var cartItems = Seq[CartItem]()
    val connection = MySqlConfig.getConnection(MySqlConnection())
    try {
      val stmt: Statement = connection.createStatement
      try {
        val rs: ResultSet = stmt.executeQuery(query)
        try {
          while (rs.next()) {
            val item = CartItem(
              rs.getInt("productId"),
              rs.getInt("quantity")
            )

            cartItems = cartItems :+ item
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
    cartItems
  }

  /**
   *
   * @param resultSet the result set obtained from the database
   * @return a sequence collected from the result set
   */
  override protected def collectData(resultSet: ResultSet): Seq[Cart] = Seq()
}
