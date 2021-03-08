package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.AddAddressModel
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait AddAddressJsonSupport extends DefaultJsonProtocol with  SprayJsonSupport with AddressJsonSupport {

  implicit val addAddressJsonProtocol: RootJsonFormat[AddAddressModel] = jsonFormat2(AddAddressModel)
}
