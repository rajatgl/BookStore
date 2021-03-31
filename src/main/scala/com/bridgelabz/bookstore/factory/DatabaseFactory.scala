package com.bridgelabz.bookstore.factory

import com.bridgelabz.bookstore.database.interfaces.ICrudRepository
import com.bridgelabz.bookstore.database.managers.upgraded.{ProductManager2, UserManager2}
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection2}
import com.bridgelabz.bookstore.database.mysql.tables.upgraded.{ProductTable2, UserTable2}
import com.bridgelabz.bookstore.models.{Otp, Product, User}

object DatabaseFactory{
  def apply(databaseName : String) = {
    databaseName.toUpperCase() match {
      case "MONGODB_USER" =>    val userCollection: ICrudRepository[User] = new DatabaseCollection2[User]("users",CodecRepository.USER)
                                val otpCollection: ICrudRepository[Otp] = new DatabaseCollection2[Otp]("userOtp",CodecRepository.OTP)
                                new UserManager2(userCollection, otpCollection)
      case "MONGODB_PRODUCT" => val userCollection: ICrudRepository[User] = new DatabaseCollection2[User]("users",CodecRepository.USER)
                                val productCollection: ICrudRepository[Product] = new DatabaseCollection2[Product]("products",CodecRepository.PRODUCT)
                                new ProductManager2(productCollection,userCollection)
      case "MYSQL_USER" =>      val userCollection: ICrudRepository[User] = new UserTable2("users")
                                val otpCollection: ICrudRepository[Otp] = new DatabaseCollection2[Otp]("userOtp",CodecRepository.OTP)
                                new UserManager2(userCollection, otpCollection)
      case "MYSQL_PRODUCT" =>   val userCollection: ICrudRepository[User] = new UserTable2("users")
                                val productCollection: ICrudRepository[Product] = new ProductTable2("products")
                                new ProductManager2(productCollection,userCollection)

    }
  }
}
