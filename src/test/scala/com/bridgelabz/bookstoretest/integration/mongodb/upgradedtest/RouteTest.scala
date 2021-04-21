package com.bridgelabz.bookstoretest.integration.mongodb.upgradedtest

import akka.http.javadsl.model.StatusCodes
import akka.http.scaladsl.model.headers.OAuth2BearerToken
import akka.http.scaladsl.model.{HttpEntity, HttpMethods, HttpRequest, MediaTypes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.bridgelabz.bookstore.database.managers.upgraded.{CartManager, ProductManager2, UserManager2, WishListManager}
import com.bridgelabz.bookstore.database.mongodb.{CodecRepository, DatabaseCollection2}
import com.bridgelabz.bookstore.jwt.TokenManager
import com.bridgelabz.bookstore.models._
import com.bridgelabz.bookstore.routes.{CartRoutes, ProductRoutes, UserRoutes, WishListRoutes}
import com.bridgelabz.bookstoretest.TestVariables
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar


/**
 * PLEASE RUN THE SUITE TEST FUNCTIONS TOGETHER
 */
class RouteTest extends AnyWordSpec
  with ScalatestRouteTest
  with MockitoSugar
  with ScalaFutures
  with BeforeAndAfterAll {

  var token: String = "invalid_token"

  val userDatabase: DatabaseCollection2[User] = new DatabaseCollection2[User](
    "userTest",
    CodecRepository.USER)

  val otpDatabase: DatabaseCollection2[Otp] = new DatabaseCollection2[Otp](
    "userOtpTest",
    CodecRepository.OTP)

  val productDatabase: DatabaseCollection2[Product] = new DatabaseCollection2[Product](
    "productTest",
    CodecRepository.PRODUCT)

  val cartDatabase: DatabaseCollection2[Cart] = new DatabaseCollection2[Cart](
    "cartTest",
    CodecRepository.CART
  )

  val wishListDatabase: DatabaseCollection2[WishList] = new DatabaseCollection2[WishList](
    "wishListTest",
    CodecRepository.WISHLIST
  )

  val userManager: UserManager2 = new UserManager2(userDatabase, otpDatabase)
  val productManager: ProductManager2 = new ProductManager2(productDatabase, userDatabase)
  val cartManager: CartManager = new CartManager(cartDatabase, userDatabase, productDatabase)
  val wishListManager: WishListManager = new WishListManager(wishListDatabase, userDatabase, productDatabase, cartDatabase)


  lazy val routes: UserRoutes = new UserRoutes(userManager)
  lazy val productRoutes: ProductRoutes = new ProductRoutes(productManager)
  lazy val cartRoutes: CartRoutes = new CartRoutes(cartManager)
  lazy val wishListRoutes: WishListRoutes = new WishListRoutes(wishListManager)

  "The service" should {

    "Routes should register a test account for a Post request to /register" in {

      val jsonRequest = ByteString(
        s"""
            {
              "userName": "${TestVariables.user().userName}",
              "mobileNumber": "${TestVariables.user().mobileNumber}",
              "email": "${TestVariables.user().email}",
              "password": "${TestVariables.user().password}"
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/register",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> routes.registerRoute ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "Routes should fail to login a test account for a Post request to /login with unverified email" in {

      val jsonRequest = ByteString(
        s"""
            {
              "email": "${TestVariables.user().email}",
              "password": "${TestVariables.user().password}"
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> routes.loginRoute ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    "Utility test to verify the user" in {
      userManager.verifyUserEmail(TestVariables.user().email)
    }

    "Route should verify a user account for Get request to /verify" in {

      Get(s"/verify?otp=${TestVariables.otp().data}&email=${TestVariables.otp().email}") ~> routes.verifyRoute ~>
        check {
          assert(status === StatusCodes.BAD_REQUEST)
        }
    }

    "Routes should login a test account for a Post request to /login" in {

      val jsonRequest = ByteString(
        s"""
            {
              "email": "${TestVariables.user().email}",
              "password": "${TestVariables.user().password}"
            }
        """.stripMargin
      )
      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/login",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> routes.loginRoute ~> check {
        assert(status === StatusCodes.OK)
        token = header("token").get.value
      }
    }

    "Route should fetch all addresses from a user account for Get request to /address" in {

      Get("/address") ~>
        addCredentials(OAuth2BearerToken(token)) ~>
        routes.getAddresses ~>
        check {
          status.equals(StatusCodes.OK)
        }
    }

    "Route should add address to a user account for Post request to /address" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "apartmentNumber": "${TestVariables.address().apartmentNumber}",
           |    "apartmentName": "${TestVariables.address().apartmentName}",
           |    "streetAddress": "${TestVariables.address().streetAddress}",
           |    "landMark": "${TestVariables.address().landMark}",
           |    "state": "${TestVariables.address().state}",
           |    "pinCode":"${TestVariables.address().pinCode}"
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/address",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> routes.addAddressRoute ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "Route should add product to a book-store for Post request to /product" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.product().productId},
           |    "author": "${TestVariables.product().author}",
           |    "title": "${TestVariables.product().title}",
           |    "image": "${TestVariables.product().image}",
           |    "quantity": ${TestVariables.product().quantity},
           |    "price": ${TestVariables.product().price},
           |    "description": "${TestVariables.product().description}"
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/addProduct",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> productRoutes.addProductRoute ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "Route should fetch all products from the book-store for Get request to /products" in {

      Get("/products") ~>
        productRoutes.getProductRoute ~>
        check {
          status.equals(StatusCodes.OK)
        }
    }
  }

  // Routes Test Cart

  // Add item test
  "This service" should {

    "Route should add item to the cart for Post request to /cart/item" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cartTest().items.head.productId},
           |    "quantity": ${TestVariables.cartTest().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/cart/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )
      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> cartRoutes.addItem ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    """Route should fail to add item to cart for
      | Post request to /wishlist/item with
      |  AccountDoesNotExistException""".stripMargin in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cartTest().items.head.productId},
           |    "quantity": ${TestVariables.cartTest().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/cart/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> cartRoutes.addItem ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    """Route should fail to add item to cart for
      | Post request to /wishlist/item with
      |  UnverifiedAccountException""".stripMargin in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cartTest().items.head.productId},
           |    "quantity": ${TestVariables.cartTest().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/cart/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      val authorization = TokenManager.generateToken(TestVariables.user().userId)

      postRequest ~> addCredentials(OAuth2BearerToken(authorization)) ~> cartRoutes.addItem ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    """Route should fail to add item to cart for
      | Post request to /wishlist/item with
      |  ProductDoesNotExistException""".stripMargin in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId},
           |    "quantity": ${TestVariables.cart().items.head.quantity}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/cart/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> cartRoutes.addItem ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }
  }

  // Get Items Test
  "This service" should {

    "Route should fetch all items from cart for Get request to /cart/items" in {

      Get("/cart/items") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        cartRoutes.getItems ~>
        check {
          status.equals(StatusCodes.OK)
        }
    }

    """Route should fail to fetch items from cart for
      | Get request to /cart/items
      | with AccountDoesNotExistException""".stripMargin in {

      Get("/cart/items") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        cartRoutes.getItems ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

    """Route should fail to fetch items from cart for
      | Get request to /cart/items
      | with UnverifiedAccountException""".stripMargin in {

      Get("/cart/items") ~>
        addCredentials(OAuth2BearerToken(TokenManager.generateToken(TestVariables.user().userId))) ~>
        cartRoutes.getItems ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

  }

  // Remove items test
  "This service " should {

    "Route should remove an item from the cart for Delete request to /cart/remove" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cartTest().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/cart/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> cartRoutes.removeItem ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    """Route should fail to remove an item from the cart for
      | Delete request to /cart/remove
      |  with AccountDoesNotExistException""".stripMargin in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/cart/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken("invalid")) ~> cartRoutes.removeItem ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }

    }

    """Route should fail to remove an item from the cart for
      | Delete request to /cart/remove
      |  with ProductDoesNotExistException""".stripMargin in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/cart/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> cartRoutes.removeItem ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }

    }

    """Route should fail to remove an item from the cart for
      | Delete request to /cart/remove
      |  with CartDoesNotExistException""".stripMargin in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.cart().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/cart/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> cartRoutes.removeItem ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }
  }
  // Routes Test WishList

  // Add item test
  "This service" should {

    "Route should add item to the cart for Post request to /wishlist/item" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishListTest().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )
      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> wishListRoutes.addItem ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "Route should fail to add item to a wish-list for Post request to /wishlist/item due to AccountDoesNotExistException" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken("invalid")) ~> wishListRoutes.addItem ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    "Route should fail to add item to a wish-list for Post request to /wishlist/item due to ProductDoesNotExistException" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/wishlist/item",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> wishListRoutes.addItem ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }
  }

  // Get Items Test
  "This service" should {

    "Route should fetch all items from wishlist for Get request to /wishlist/items" in {

      Get("/wishlist/items") ~>
        addCredentials(OAuth2BearerToken(token)) ~>
        wishListRoutes.getItems ~>
        check {
          status.equals(StatusCodes.OK)
        }
    }

    "Route should fail to fetch items from the wishlist for Get request to /wishlist/items due to AccountDoesNotExistException" in {

      Get("/wishlist/items") ~>
        addCredentials(OAuth2BearerToken(token)) ~>
        wishListRoutes.getItems ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

    "Route should fail to fetch items from the wishlist for Get request to /wishlist/items due to UnverifiedAccountException" in {

      Get("/wishlist/items") ~>
        addCredentials(OAuth2BearerToken(token)) ~>
        wishListRoutes.getItems ~>
        check {
          status.equals(StatusCodes.UNAUTHORIZED)
        }
    }

  }

  // Remove items test
  "This service " should {

    "Route should remove an item to a wish-list for Delete request to /wishlist/remove" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishListTest().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/wishlist/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> wishListRoutes.removeItem ~> check {
        assert(status === StatusCodes.OK)
      }
    }

    "Route should fail to remove an item to a wish-list for Delete request to /wishlist/remove due to AccountDoesNotExistException" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/wishlist/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken("invalid")) ~> wishListRoutes.removeItem ~> check {
        assert(status === StatusCodes.UNAUTHORIZED)
      }
    }

    "Route should fail to remove an item to a wish-list for Delete request to /wishlist/remove due to ProductDoesNotExistException" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/wishlist/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> wishListRoutes.removeItem ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }

    "Route should fail to remove an item to a wish-list for Delete request to /wishlist/remove due to WishListDoesNotExistException" in {

      val jsonRequest = ByteString(
        s"""
           |{
           |    "productId": ${TestVariables.wishList().items.head.productId}
           |}
           |""".stripMargin
      )

      val postRequest = HttpRequest(
        HttpMethods.DELETE,
        uri = "/wishlist/remove",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )

      postRequest ~> addCredentials(OAuth2BearerToken(token)) ~> wishListRoutes.removeItem ~> check {
        assert(status === StatusCodes.NOT_FOUND)
      }
    }
    "utility to delete databases" in {
      userDatabase.collection.drop()
      otpDatabase.collection.drop()
      productDatabase.collection.drop()
      cartDatabase.collection.drop()
      wishListDatabase.collection.drop()
    }
  }


}
