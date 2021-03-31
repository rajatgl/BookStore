package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.ProductIdModel
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait ProductIdJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val removeItemJsonProtocol: RootJsonFormat[ProductIdModel] = jsonFormat1(ProductIdModel)
}
