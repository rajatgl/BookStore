package com.bridgelabz.bookstore.jwt

import java.util.concurrent.TimeUnit

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}

/**
 * Created on 3/6/2021.
 * Class: TokenManager.scala
 * Author: Rajat G.L.
 */
object TokenManager {
  /**
   *
   * @param identifier string to be tokenized
   * @param tokenExpiryPeriodInDays token duration in days
   * @param header jwtHeader( encryption method)
   * @param secretKey for encryption
   * @return token
   */
  def generateToken(identifier: String,
                    tokenExpiryPeriodInDays: Int = sys.env("TOKEN_EXPIRY_IN_DAYS").toInt,
                    header: JwtHeader = JwtHeader(sys.env("ENCRYPTION_TYPE")),
                    secretKey: String = sys.env("SECRET_KEY")): String = {

    val payLoad = JwtClaimsSet(
      Map(
        "identifier" -> identifier,
        "expiredAt" -> (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(tokenExpiryPeriodInDays))
      )
    )
    JsonWebToken(header, payLoad, secretKey)
  }

  /**
   *
   * @param token to check if its expired
   * @return boolean result of the same
   */
  def isTokenExpired(token: String): Boolean =
    getPayloadAsMap(token).get("expiredAt").exists(_.toLong < System.currentTimeMillis())

  /**
   *
   * @param token to be claimed
   * @return if all tokens claimed, return an empty map else return the tokens/claims remaining
   */
  def getPayloadAsMap(token: String): Map[String, String] =
    JsonWebToken.unapply(token) match {
      case Some(value) => value._2.asSimpleMap.get
      case None => Map.empty[String, String]
    }

  /**
   *
   * @param jwtToken the jwt string to be verified
   * @return if the token is valid or not
   */
  def isValidToken(jwtToken: String): Boolean =
    !isTokenExpired(jwtToken) && JsonWebToken.validate(jwtToken, System.getenv("SECRET_KEY"))

  /**
   *
   * @param jwtToken the jwt string that contains the required payload
   * @return identified field of the payload
   */
  def getIdentifier(jwtToken: String): String =
    TokenManager.getPayloadAsMap(jwtToken)("identifier")
}
