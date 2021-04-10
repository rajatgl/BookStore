package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.PlaceOrderModel
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait PlaceOrderJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val placeOrderJsonProtocol: RootJsonFormat[PlaceOrderModel] = jsonFormat2(PlaceOrderModel)

}
