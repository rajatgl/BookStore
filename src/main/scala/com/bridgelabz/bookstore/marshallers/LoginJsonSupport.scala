package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.LoginModel
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait LoginJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val loginJsonProtocol: RootJsonFormat[LoginModel] = jsonFormat2(LoginModel)
}
