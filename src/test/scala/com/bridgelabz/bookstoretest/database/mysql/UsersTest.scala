package com.bridgelabz.bookstoretest.database.mysql

import com.bridgelabz.bookstore.database.interfaces.ICrud
import com.bridgelabz.bookstore.database.mysql.tables.UserTable
import com.bridgelabz.bookstore.models.User
import com.bridgelabz.bookstoretest.TestVariables
import com.dimafeng.testcontainers.{ForAllTestContainer, MySQLContainer}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime

import scala.concurrent.Await

class UsersTest extends AnyFlatSpec with ForAllTestContainer with Matchers {

  override val container: MySQLContainer = MySQLContainer()
  val userTable: ICrud[User] = new UserTable("test")
  Class.forName(container.driverClassName)

  it should "add a single user to the table" in {
    assert(Await.result(userTable.create(
      TestVariables.user()), 1500.seconds).asInstanceOf[Boolean])
  }

  it should "read the products from the table" in {
    assert(Await.result(userTable.read(), 1500.seconds).length === 1)
  }

  it should "update the product in the table" in {
    assert(Await.result(userTable.update(
      TestVariables.user().userId,TestVariables.user(),"userId"
    ), 1500.seconds).asInstanceOf[Boolean])
  }


  it should "delete the product from the table" in {
    assert(Await.result(userTable.delete(
      TestVariables.user().userName,"userName"
    ), 1500.seconds).asInstanceOf[Boolean])
  }
}
