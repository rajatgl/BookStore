package com.bridgelabz.bookstore.factory

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.upgraded.{ProductManager2, UserManager2, WishListManager}
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection2}
import com.bridgelabz.bookstore.database.mysql.tables.CartTable
import com.bridgelabz.bookstore.database.mysql.tables.upgraded.{CartTableById, ProductTable2, UserTable2, WishListTableById}
import com.bridgelabz.bookstore.models.{Cart, Otp, Product, User, WishList}

object DatabaseFactory{
  val otpCollection: ICrudRepository[Otp] = new DatabaseCollection2[Otp]("userOtp",CodecRepository.OTP)
  def apply(databaseName: DatabaseEnums.Value) = {
    databaseName match {
      case DatabaseEnums.MONGODB_USER => val userCollection: ICrudRepository[User] = getUserDatabase("mongodb")
                                          new UserManager2(userCollection, otpCollection)
      case DatabaseEnums.MONGODB_PRODUCT => val userCollection: ICrudRepository[User] = getUserDatabase("mongodb")
                                            val productCollection: ICrudRepository[Product] = getProductDatabase("mongodb")
                                            new ProductManager2(productCollection,userCollection)
      case DatabaseEnums.MONGODB_WISHLIST => val userCollection: ICrudRepository[User] = getUserDatabase("mongodb")
                                             val productCollection: ICrudRepository[Product] = getProductDatabase("mongodb")
                                             val cartCollection: ICrudRepository[Cart] = getCartDatabase("mongodb")
                                             val wishListCollection: ICrudRepository[WishList] = getWishListDatabase("mongodb")
                                             new WishListManager(wishListCollection, userCollection, productCollection, cartCollection)
      case DatabaseEnums.MYSQL_USER =>      val userCollection: ICrudRepository[User] = getUserDatabase("mysql")
                                            new UserManager2(userCollection, otpCollection)
      case DatabaseEnums.MYSQL_PRODUCT =>   val userCollection: ICrudRepository[User] = getUserDatabase("mysql")
                                            val productCollection: ICrudRepository[Product] = getProductDatabase("mysql")
                                            new ProductManager2(productCollection,userCollection)
      case DatabaseEnums.MYSQL_WISHLIST => val userCollection: ICrudRepository[User] = getUserDatabase("mysql")
                                            val productCollection: ICrudRepository[Product] = getProductDatabase("mysql")
                                            val cartCollection: ICrudRepository[Cart] = getCartDatabase("mysql")
                                            val wishListCollection: ICrudRepository[WishList] = getWishListDatabase("mysql")
                                            new WishListManager(wishListCollection, userCollection, productCollection, cartCollection)

    }
  }

  def getProductDatabase(databaseType : String) : ICrudRepository[Product] = {
    databaseType.toUpperCase() match {
      case "MONGODB" => new DatabaseCollection2[Product]("products",CodecRepository.PRODUCT)
      case "MYSQL" => new ProductTable2("products")
    }
  }
  def getUserDatabase(databaseType : String) : ICrudRepository[User] = {
    databaseType.toUpperCase() match {
      case "MONGODB" => new DatabaseCollection2[User]("users",CodecRepository.USER)
      case "MYSQL" => new UserTable2("users")
    }
  }
  def getCartDatabase(databaseType : String) : ICrudRepository[Cart] = {
    databaseType.toUpperCase() match {
      case "MONGODB" => new DatabaseCollection2[Cart]("carts",CodecRepository.CART)
      case "MYSQL" => new CartTableById("carts","products")
    }
  }
  def getWishListDatabase(databaseType : String) : ICrudRepository[WishList] = {
    databaseType.toUpperCase() match {
      case "MONGODB" => new DatabaseCollection2[WishList]("wishlist",CodecRepository.WISHLIST)
      case "MYSQL" => new WishListTableById("wishlist","products")
    }
  }
}
