package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.Price
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait PriceJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val priceJsonProtocol: RootJsonFormat[Price] = jsonFormat3(Price)
}
