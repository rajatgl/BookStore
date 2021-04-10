package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.CartItem
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait CartItemJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val cartProductJsonProtocol: RootJsonFormat[CartItem] = jsonFormat3(CartItem)

}
