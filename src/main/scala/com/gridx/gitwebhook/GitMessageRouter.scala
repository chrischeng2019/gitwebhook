package com.gridx.gitwebhook

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.post
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.directives.PathDirectives.path
import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout
import com.gridx.gitwebhook.GitMessageHandlerActor._
import spray.json.DefaultJsonProtocol._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._



trait GitMessageRouter {

  implicit def system: ActorSystem

  lazy val log = Logging(system, classOf[GitMessageRouter])

  def gitMessageHandlerActor: ActorRef

  implicit lazy val timeout = Timeout(5.seconds)


  implicit val gitUserFormat = jsonFormat2(GitUser)
  implicit val gitcommitFormat = jsonFormat4(GitCommit)
  implicit val gitPushMessageFormat = jsonFormat1(GitPushMessage)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  val route: Route =
    post {
      path("validate-checkin-comments") {
        entity(as[GitPushMessage]) { message =>
          val userCreated: Future[ActionPerformed] =
            (gitMessageHandlerActor ? ValidateGitMessage(message)).mapTo[ActionPerformed]
            onSuccess(userCreated) { performed =>
            //log.info("validated message:" + message)
            complete("validated check-in comments")
          }
        }
      }
    }

}
