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
                    tokenExpiryPeriodInDays: Int = System.getenv("TOKEN_EXPIRY_IN_DAYS"),
                    header: JwtHeader = JwtHeader(System.getenv("ENCRYPTION_TYPE")),
                    secretKey: String = System.getenv("SECRET_KEY")): String = {

    val claimSet = JwtClaimsSet(
      Map(
        "identifier" -> identifier,
        "expiredAt" -> (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(tokenExpiryPeriodInDays))
      )
    )
    JsonWebToken(header, claimSet, secretKey)
  }
}
