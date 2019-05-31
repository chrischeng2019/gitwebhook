package com.gridx.gitwebhook

import java.util.Properties

import com.gridx.gitwebhook.Encryption.EncryptionKEY
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeMessage}
import com.typesafe.config._


object Mailer {

  final val EmailHost = "email.host"
  final val EmailPort = "email.port"
  final val EmailSender = "email.sender"
  final val EmailUserName = "email.username"
  final val EmailUserPwd = "email.pwd"
  final val EmailCCList = "email.cclist"

  val conf = ConfigFactory.load()
  val host = conf.getString(EmailHost)
  val port = conf.getString(EmailPort)
  var senderAddress = conf.getString(EmailSender)
  val username = conf.getString(EmailUserName)
  val password = Encryption.decrypt(conf.getString(EmailUserPwd))
  val ccemaillist = conf.getString(EmailCCList).split(",")


  def sendMail(text: String, subject: String, recipientAddress: String) = {
    val properties = new Properties()
    properties.put("mail.smtp.port", port)
    properties.put("mail.smtp.auth", "true")
    properties.put("mail.smtp.starttls.enable", "true")


    val session = Session.getDefaultInstance(properties, null)
    val message = new MimeMessage(session)
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
    message.setSubject(subject)
    message.setSender(new InternetAddress(senderAddress))
    ccemaillist.foreach(email =>   message.addRecipient(Message.RecipientType.CC, new InternetAddress(email)))
    message.setContent(text, "text/plain")


    val transport = session.getTransport("smtp")
    transport.connect(host, username, password)
   // println("Sending email....")
   // println(text)
    transport.sendMessage(message, message.getAllRecipients)
  }

  def main(args: Array[String]) = {

    sendMail("aaaa1", "bbb", "hong@gridx.com")
  }

}