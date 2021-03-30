package com.bridgelabz.bookstore.database.mongodb

import com.bridgelabz.bookstore.models._
import org.bson.codecs.configuration.{CodecProvider, CodecRegistries, CodecRegistry}
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY

/**
 * Created on 3/7/2021.
 * Class: CodecRepository.scala
 * Author: Ruchir Dixit
 */
object CodecRepository extends Enumeration {

  type CodecNames = Value
  val USER, OTP, PRODUCT, CART, WISHLIST = Value


  private val codecProviderForUser: CodecProvider = Macros.createCodecProvider[User]()
  private val codecProviderForAddress: CodecProvider = Macros.createCodecProvider[Address]()
  private val codecProviderForCart: CodecProvider = Macros.createCodecProvider[Cart]()
  private val codecProviderForCartItems: CodecProvider = Macros.createCodecProvider[CartItem]()
  private val codecProviderForWishList: CodecProvider = Macros.createCodecProvider[WishList]()
  private val codecProviderForWishListItems: CodecProvider = Macros.createCodecProvider[WishListItem]()

  private val codecRegistryForUser: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(codecProviderForUser, codecProviderForAddress),
    DEFAULT_CODEC_REGISTRY
  )

  private val codecRegistryForCart: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(codecProviderForCart, codecProviderForCartItems),
    DEFAULT_CODEC_REGISTRY
  )
  private val codecRegistryForWishList: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(codecProviderForWishList, codecProviderForWishListItems),
    DEFAULT_CODEC_REGISTRY
  )
  private val codecProviderForOtp: CodecProvider = Macros.createCodecProvider[Otp]()
  private val codecRegistryForOtp: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(codecProviderForOtp),
    DEFAULT_CODEC_REGISTRY
  )

  private val codecProviderForProduct: CodecProvider = Macros.createCodecProvider[Product]()
  private val codecRegistryForProduct: CodecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromProviders(codecProviderForProduct),
    DEFAULT_CODEC_REGISTRY
  )

  def getCodecRegistry(codecName: CodecNames): CodecRegistry = {
    codecName match{
      case USER => codecRegistryForUser
      case OTP => codecRegistryForOtp
      case PRODUCT => codecRegistryForProduct
      case CART => codecRegistryForCart
      case WISHLIST => codecRegistryForWishList
    }
  }

}
