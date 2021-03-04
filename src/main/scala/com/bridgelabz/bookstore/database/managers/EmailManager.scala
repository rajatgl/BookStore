package com.bridgelabz.bookstore.database.managers

import com.bridgelabz.bookstore.models.Otp
import com.typesafe.scalalogging.Logger
import courier.{Envelope, Mailer, Text}
import javax.mail.internet.InternetAddress

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object EmailManager {

  private val logger = Logger("EmailManager")

  def sendOtp(otp: Otp): Unit ={

    val body: String = s"<a href='http://${System.getenv("Host")}:${System.getenv("Port")}/verify?otp=${otp.data}&email=${otp.email}'>Click here</a> to verify your email."
    val subject: String = "Email Verification"

    sendEmail(otp.email, subject, body)
  }

  def sendEmail(email: String,
                subject: String,
                body: String,
                mailProtocol: String = System.getenv("EMAIL_PROTOCOL"),
                mailStatusCode: Int = System.getenv("EMAIL_CODE").toInt): Unit = {

    val mailer = Mailer(mailProtocol, mailStatusCode)
      .auth(true)
      .as(System.getenv("SENDER_EMAIL"), System.getenv("SENDER_PASSWORD"))
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
