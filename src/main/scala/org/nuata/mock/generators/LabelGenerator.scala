package org.nuata.mock.generators

import org.nuata.mock.generators.base.Word
import org.nuata.models.Label

/**
 * Created by nico on 16/02/16.
 */
object LabelGenerator extends Generator[Label]("label") {
  def generate() = {
    val lang = Lang.generate()
    val name = if(getBoolean("hasName")) Some(Word.generate()) else None
    val description = if(getBoolean("hasDescription")) Some(Word.generate()) else None
    val aliases = generateSeq("nbAliases", Word.generate())
    Label(lang, name, description, aliases.toArray)
  }
}
