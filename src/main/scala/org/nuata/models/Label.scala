package org.nuata.models

/**
  * Created by nico on 13/02/16.
  */
case class Label(lang: String, name: Option[String], description: Option[String], aliases: Array[String],
                  var suggestName: Option[SuggestIndex] = None,
                  var suggestAlias: Option[SuggestIndex] = None) {
                   }
