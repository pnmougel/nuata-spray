package org.nuata.models

/**
  * Created by nico on 18/02/16.
  */
case class SuggestIndex(input: Array[String], output: Option[String] = None, payload: Option[Map[String, Option[String]]] = None, weight: Int = 1)
