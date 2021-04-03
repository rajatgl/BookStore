package com.bridgelabz.bookstore.factory

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection2}
import com.bridgelabz.bookstore.database.mysql.tables.upgraded.{CartTableById, ProductTable2, UserTable2, WishListTableById}
import com.bridgelabz.bookstore.models._
import Collections._

object DatabaseFactory {

  val otpCollection: ICrudRepository[Otp] = new DatabaseCollection2[Otp]("userOtp", CodecRepository.OTP)

  def apply[T](collection: Collections.Value, database: Databases.Value): ICrudRepository[T] = {
    collection match {
      case USER => getUserCollection(database).asInstanceOf
      case OTP => otpCollection.asInstanceOf
      case PRODUCT => getProductCollection(database).asInstanceOf
      case WISHLIST => getWishListCollection(database).asInstanceOf
      case CART => getCartCollection(database).asInstanceOf
    }
  }

  def getProductCollection(databaseType: Databases.Value): ICrudRepository[Product] = {
    databaseType match {
      case Databases.MONGODB => new DatabaseCollection2[Product]("products", CodecRepository.PRODUCT)
      case Databases.MYSQL => new ProductTable2("products")
    }
  }

  def getUserCollection(databaseType: Databases.Value): ICrudRepository[User] = {
    databaseType match {
      case Databases.MONGODB => new DatabaseCollection2[User]("users", CodecRepository.USER)
      case Databases.MYSQL => new UserTable2("users")
    }
  }

  def getCartCollection(databaseType: Databases.Value): ICrudRepository[Cart] = {
    databaseType match {
      case Databases.MONGODB => new DatabaseCollection2[Cart]("carts", CodecRepository.CART)
      case Databases.MYSQL => new CartTableById("carts", "products")
    }
  }

  def getWishListCollection(databaseType: Databases.Value): ICrudRepository[WishList] = {
    databaseType match {
      case Databases.MONGODB => new DatabaseCollection2[WishList]("wishlist", CodecRepository.WISHLIST)
      case Databases.MYSQL => new WishListTableById("wishlist", "products")
    }
  }
}
