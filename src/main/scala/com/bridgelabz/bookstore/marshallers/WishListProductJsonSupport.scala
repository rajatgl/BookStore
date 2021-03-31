package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.WishListProduct
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait WishListProductJsonSupport extends DefaultJsonProtocol with SprayJsonSupport with AddProductJsonSupport {

  implicit val wishlistProductJsonProtocol: RootJsonFormat[WishListProduct] = jsonFormat2(WishListProduct)
}
