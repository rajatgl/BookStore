package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.Order
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait OrderJsonSupport extends DefaultJsonProtocol with SprayJsonSupport with CartItemJsonSupport with AddressJsonSupport {
  implicit val orderJsonProtocol: RootJsonFormat[Order] = jsonFormat8(Order)
}
