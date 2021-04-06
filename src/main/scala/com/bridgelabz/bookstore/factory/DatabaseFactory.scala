package com.bridgelabz.bookstore.factory

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection2}
import com.bridgelabz.bookstore.database.mysql.tables.upgraded.{CartTableById, ProductTable2, UserTable2, WishListTableById}
import com.bridgelabz.bookstore.models._
import Collections._

/**
 * Database Factory Object for providing required database objects
 * Available Databases: MongoDB/MySQL
 */
object DatabaseFactory {

  val otpCollection: ICrudRepository[Otp] = new DatabaseCollection2[Otp]("userOtp", CodecRepository.OTP)

  /**
   *
   * @param collection name of the collection/table to be fetched
   * @param database name of the database to be fetched
   * @tparam T the type of objects the collection stores as documents/ records
   * @return the required database of type ICrudRepository[T]
   */
  def apply[T](collection: Collections.Value, database: Databases.Value): ICrudRepository[T] = {
    collection match {
      case USER => getUserCollection(database).asInstanceOf[ICrudRepository[T]]
      case OTP => otpCollection.asInstanceOf[ICrudRepository[T]]
      case PRODUCT => getProductCollection(database).asInstanceOf[ICrudRepository[T]]
      case WISHLIST => getWishListCollection(database).asInstanceOf[ICrudRepository[T]]
      case CART => getCartCollection(database).asInstanceOf[ICrudRepository[T]]
    }
  }

  /**
   *
   * @param databaseType choose from the Enum of available databases
   * @return the product collection/ table
   */
  def getProductCollection(databaseType: Databases.Value): ICrudRepository[Product] = {
    databaseType match {
      case Databases.MONGODB => new DatabaseCollection2[Product]("products", CodecRepository.PRODUCT)
      case Databases.MYSQL => new ProductTable2("products")
    }
  }

  /**
   *
   * @param databaseType choose from the Enum of available databases
   * @return the user collection/ table
   */
  def getUserCollection(databaseType: Databases.Value): ICrudRepository[User] = {
    databaseType match {
      case Databases.MONGODB => new DatabaseCollection2[User]("users", CodecRepository.USER)
      case Databases.MYSQL => new UserTable2("users")
    }
  }

  /**
   *
   * @param databaseType choose from the Enum of available databases
   * @return the cart collection/ table
   */
  def getCartCollection(databaseType: Databases.Value): ICrudRepository[Cart] = {
    databaseType match {
      case Databases.MONGODB => new DatabaseCollection2[Cart]("carts", CodecRepository.CART)
      case Databases.MYSQL => new CartTableById("carts", "products","users")
    }
  }

  /**
   *
   * @param databaseType choose from the Enum of available databases
   * @return the wishlist collection/ table
   */
  def getWishListCollection(databaseType: Databases.Value): ICrudRepository[WishList] = {
    databaseType match {
      case Databases.MONGODB => new DatabaseCollection2[WishList]("wishlist", CodecRepository.WISHLIST)
      case Databases.MYSQL => new WishListTableById("wishlist", "products", "users")
    }
  }
}