package org.nuata.shared.json

import org.json4s.JsonAST._

object OptExtractors {
  object JDoubleOpt {
    def unapply(e: Any) = e match {
      case d: JDouble => Some(JDouble.unapply(d))
      case _ => Some(None)
    }
  }

  object JStringOpt {
    def unapply(e: Any) = e match {
      case d: JString => Some(JString.unapply(d))
      case _ => Some(None)
    }
  }

  object JIntOpt {
    def unapply(e: Any) = e match {
      case d: JInt => Some(JInt.unapply(d))
      case _ => Some(None)
    }
  }

  object JBooleanOpt {
    def unapply(e: Any) = e match {
      case d: JBool => Some(JBool.unapply(d))
      case _ => Some(None)
    }
  }

  object JArrayOpt {
    def unapply(e: Any) = e match {
      case d: JArray => Some(JArray.unapply(d))
      case _ => Some(None)
    }
  }

  object JObjectOpt {
    def unapply(e: Any) = e match {
      case d: JObject => Some(JObject.unapply(d))
      case _ => Some(None)
    }
  }

//  object JFieldOpt {
//    def unapply(e: Any) = e match {
//      case d: JField => Some(JField.unapply(d))
//      case _ => Some(None)
//    }
//  }
}


