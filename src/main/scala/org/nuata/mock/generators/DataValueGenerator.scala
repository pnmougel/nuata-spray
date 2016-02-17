package org.nuata.mock.generators

import org.nuata.mock.generators.base.Word
import org.nuata.models.datavalues.{StringDataValue, DataValue}
/**
 * Created by nico on 17/02/16.
 */
object DataValueGenerator extends Generator[DataValue]("dataValue") {

  def generate() : DataValue = {
    val valueType = getString("valueType")
    valueType match {
      case "string" => {
        StringDataValue(Word.generate())
      }
      case _ => {
        StringDataValue(Word.generate())
      }
    }
  }
}

