package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.Product
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait AddProductJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val addressJsonProtocol: RootJsonFormat[Product] = jsonFormat7(Product)
}
