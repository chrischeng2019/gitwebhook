package com.gridx.gitwebhook

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging
import com.gridx.gitwebhook.CommentsValidator.isValidComments
import com.gridx.gitwebhook.Mailer.sendMail

/**
  * *************************************
  * The actor is used validate git message
  ***************************/

// domain model
final case class GitUser(name: String, email: String)

final case class GitCommit(id: String, message: String, timestamp : String,  author: GitUser)

final case class GitPushMessage(commits: List[GitCommit])


object GitMessageHandlerActor {

  final case class ActionPerformed(description: String)

  final case class ValidateGitMessage(message: GitPushMessage)

  def props: Props = Props[GitMessageHandlerActor]
}


class GitMessageHandlerActor extends Actor with ActorLogging {

  import GitMessageHandlerActor._

  override val  log = Logging(context.system, this)


  def receive: Receive = {

    case ValidateGitMessage(message) =>
      validate(message);
      sender() ! ActionPerformed(s"Git Msg validated.")

  }

  def validate(message: GitPushMessage) = {
    message.commits.foreach(commit => sendNotificationEmail(commit))
  }

  def sendNotificationEmail(commit: GitCommit): Unit = {
    if (!isValidComments(commit.message)) {
      log.info("Notify Policy Voilator: " + commit)
      sendMail(genEmailBody(commit.author.name, commit.author.email, commit.id, commit.timestamp, commit.message), "Check-in Policy Violation", commit.author.email)
    }
  }

  def genEmailBody(user: String, email: String, id: String, timestamp : String, message : String): String = {
    "Hi " + user + "\r\n\r\n" +
      "Your check-in comments does not contains JIRA ID, which violates the company check-in policy.\r\n" +
      "id : " + id  + "\r\n" +
      "comments : " + message  + "\r\n" +
       "timestamp : " + timestamp + "\r\n" +
      "Your check-in comments should contain valid JIRA Id enclosed with brackets, e.g. [SCE-1234]\r\n\r\n" +
      "Gridx Dev team"

  }

}




