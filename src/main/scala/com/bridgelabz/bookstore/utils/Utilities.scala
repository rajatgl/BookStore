package com.bridgelabz.bookstore.utils

import scala.util.Random

/**
 * Created on 3/4/2021.
 * Class: Utilities.scala
 * Author: Rajat G.L.
 */
object Utilities {

  /**
   *
   * @return a random integer
   */
  def randomNumber(): Int = {

    Math.abs(Random.nextInt())
  }
}
