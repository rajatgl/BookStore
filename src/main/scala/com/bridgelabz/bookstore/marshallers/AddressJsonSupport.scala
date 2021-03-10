package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.Address
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait AddressJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val addressJsonProtocol: RootJsonFormat[Address] = jsonFormat6(Address)
}
