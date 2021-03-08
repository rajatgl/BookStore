package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.RegisterModel
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait RegisterJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val registerJsonProtocol: RootJsonFormat[RegisterModel] = jsonFormat4(RegisterModel)
}
