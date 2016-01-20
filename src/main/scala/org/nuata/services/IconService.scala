package org.nuata.services

import java.awt._
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import org.json4s.Extraction
import org.nuata.directives.CorsSupport
import org.nuata.models._
import org.nuata.models.queries.UserAccountQuery
import org.nuata.repositories._
import org.nuata.services.routing.RouteRegistration
import org.nuata.shared.Json4sProtocol
import spray.http.{MediaTypes, MediaType}
import spray.http.StatusCodes._
import spray.routing._
import scala.concurrent.ExecutionContext.Implicits.global

import MediaType._
import MediaTypes._

/**
 * Created by nico on 30/12/15.
 */
trait IconService extends RouteRegistration with Json4sProtocol {
  val fontInputStream = this.getClass.getResourceAsStream("/fonts/Questrial-Regular.ttf")

  val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
  val questrialFont = Font.createFont(Font.TRUETYPE_FONT, fontInputStream)
  ge.registerFont(questrialFont)

  def getIcon(size: Int, format: String): File = {
    val outputDirPath = "cache/icon/"
    val outputFile = new File(s"${outputDirPath}icon_${size}.${format}")
    val useCache = false
    if (!outputFile.exists() || !useCache) {
      val bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
      val g = bi.getGraphics.asInstanceOf[Graphics2D]
      g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
       g.setColor(Color.RED)
      val roundRadius = Math.max(4, size / 9)
      g.fillRoundRect(0, 0, size, size, roundRadius, roundRadius)
      val font = questrialFont.deriveFont(Font.PLAIN, (size * 1.2).toInt)
      g.setFont(font)
      val width = g.getFontMetrics.charWidth('n')
      g.setColor(Color.WHITE)
      g.drawString("n", ((size.toFloat - width) / 2), size)
      g.dispose()

      val imagePath = new File(outputDirPath)
      if(!imagePath.exists()) {
        imagePath.mkdirs()
      }
      ImageIO.write(bi, format, outputFile)
    }
    outputFile
  }

  val validImageFormats = Set("png", "jpg", "svg")
  val sizeNameToSize = Map[String, Int]("tiny" -> 16, "small" -> 32, "medium" -> 64, "large" -> 128)

  registerRoute {
    (path("icon" / IntNumber / Segment.?) & get) { (size, format) =>
      val imgFormat = (for(imgFormat <- format; if validImageFormats.contains(imgFormat.toLowerCase)) yield {
        imgFormat.toLowerCase
      }).getOrElse("png")
      getFromFile(getIcon(size, imgFormat))
    } ~ (path("icon" / IntNumber) & get) { size =>
      getFromFile(getIcon(size, "png"))
    } ~ (path("icon" / Segment.?) & get) { size =>
      val imgSize = (for(imgSize <- size; s <- sizeNameToSize.get(imgSize.toLowerCase)) yield {
        s
      }).getOrElse(16)
      getFromFile(getIcon(imgSize, "png"))
    }
  }
}
