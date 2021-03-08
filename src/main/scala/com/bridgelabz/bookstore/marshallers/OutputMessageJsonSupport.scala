package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.OutputMessage
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait OutputMessageJsonSupport extends DefaultJsonProtocol with SprayJsonSupport{
  implicit val outputMessageJsonFormat: RootJsonFormat[OutputMessage] = jsonFormat2(OutputMessage)
}
