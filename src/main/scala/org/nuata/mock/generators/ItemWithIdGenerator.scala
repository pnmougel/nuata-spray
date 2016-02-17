package org.nuata.mock.generators

import java.util.UUID

import org.nuata.models.Item

/**
  * Created by nico on 17/02/16.
  */
object ItemWithIdGenerator extends Generator[Item]("item") {
   def generate() : Item = {
     ItemGenerator.generate().copy(_id = Some(UUID.randomUUID().toString))
   }
 }
