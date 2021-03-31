package com.bridgelabz.bookstore.marshallers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.bridgelabz.bookstore.models.WishListItem
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait WishlistItemJsonSupport extends DefaultJsonProtocol with SprayJsonSupport{

  implicit val wishlistItemJsonProtocol: RootJsonFormat[WishListItem] = jsonFormat2(WishListItem)

}
