package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.CartProduct
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait CartProductJsonSupport extends DefaultJsonProtocol with SprayJsonSupport with AddProductJsonSupport {

  implicit val cartProductJsonProtocol: RootJsonFormat[CartProduct] = jsonFormat3(CartProduct)
}
