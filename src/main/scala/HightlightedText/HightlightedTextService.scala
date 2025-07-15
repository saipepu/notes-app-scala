package HightlightedText

import zio.{UIO, ULayer, ZIO, ZLayer}

trait HightlightedTextService {
  def getAllHightlightedText: UIO[String]
}

object HightlightedTextService {
  val live: ULayer[HightlightedTextService] = ZLayer.succeed(
    new HightlightedTextService {
      override def getAllHightlightedText: UIO[String] = ZIO.succeed("All the HightlightedText")
    }
  )
}
