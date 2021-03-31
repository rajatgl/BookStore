package com.bridgelabz.bookstore.factory

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.upgraded.{ProductManager2, UserManager2}
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection2}
import com.bridgelabz.bookstore.database.mysql.tables.upgraded.{ProductTable2, UserTable2}
import com.bridgelabz.bookstore.models.{Otp, Product, User}

object DatabaseFactory{
  val otpCollection: ICrudRepository[Otp] = new DatabaseCollection2[Otp]("userOtp",CodecRepository.OTP)
  def apply(databaseName : String) = {
    databaseName.toUpperCase() match {
      case "MONGODB_USER" =>    val userCollection: ICrudRepository[User] = getUserDatabase("mongodb")
                                new UserManager2(userCollection, otpCollection)
      case "MONGODB_PRODUCT" => val userCollection: ICrudRepository[User] = getUserDatabase("mongodb")
                                val productCollection: ICrudRepository[Product] = getProductDatabase("mongodb")
                                new ProductManager2(productCollection,userCollection)
      case "MYSQL_USER" =>      val userCollection: ICrudRepository[User] = getUserDatabase("mysql")
                                new UserManager2(userCollection, otpCollection)
      case "MYSQL_PRODUCT" =>   val userCollection: ICrudRepository[User] = getUserDatabase("mysql")
                                val productCollection: ICrudRepository[Product] = getProductDatabase("mysql")
                                new ProductManager2(productCollection,userCollection)

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
}
