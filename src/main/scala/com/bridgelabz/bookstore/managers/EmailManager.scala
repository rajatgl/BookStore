package com.bridgelabz.bookstore.managers

import com.bridgelabz.bookstore.models.Otp
import com.typesafe.scalalogging.Logger
import courier.{Envelope, Mailer, Text}
import javax.mail.internet.InternetAddress

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * Created on 3/6/2021.
 * Class: EmailManager.scala
 * Author: Rajat G.L.
 */
object EmailManager {

  private val logger = Logger("EmailManager")

  /**
   *
   * @param otp object to be sent for email verification
   */
  def sendOtp(otp: Otp): Unit = {

    val body: String =
      s"""
         | Click here: http://${System.getenv("HOST")}:${System.getenv("PORT")}/verify?otp=${otp.data}&email=${otp.email}
         |  to verify your email.""".stripMargin
    val subject: String = "Email Verification"

    sendEmail(otp.email, subject, body)
  }

  /**
   *
   * @param email of the recipient
   * @param body  of the email to be sent
   */
  def sendEmail(email: String,
                subject: String,
                body: String,
                mailProtocol: String = sys.env("MAIL_PROTOCOL"),
                mailStatusCode: Int = sys.env("MAIL_STATUS_CODE").toInt): Unit = {

    val mailer = Mailer(mailProtocol, mailStatusCode)
      .auth(true)
      .as(sys.env("SENDER_EMAIL"), sys.env("SENDER_PASSWORD"))
      .startTls(true)()
    mailer(Envelope.from(new InternetAddress(System.getenv("SENDER_EMAIL")))
      .to(new InternetAddress(email))
      .subject(subject)
      .content(Text(s"$body\nHappy to serve you!")))
      .onComplete {
        case Success(_) => logger.info(s"Notification email sent to $email")
        case Failure(exception) => logger.error(s"Email could not be sent: ${exception.getMessage}")
      }
  }
}
