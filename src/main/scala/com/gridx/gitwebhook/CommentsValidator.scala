package com.gridx.gitwebhook

import scala.util.matching.Regex

object CommentsValidator {
  val commentsPattern: Regex = {
    ".*\\[\\w+-\\d+\\].*".r
  }

  def isValidComments(comments: String): Boolean = {
    !commentsPattern.findFirstMatchIn(comments).isEmpty
  }
}
