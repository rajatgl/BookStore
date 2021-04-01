package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.{CartProductIdModel, ProductIdModel}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait CartProductIdJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val cartProductIdJsonProtocol: RootJsonFormat[CartProductIdModel] = jsonFormat2(CartProductIdModel)
}
