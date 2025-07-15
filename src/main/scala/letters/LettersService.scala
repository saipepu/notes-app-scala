package letters

import zio.{UIO, ULayer, ZIO, ZLayer}

trait LettersService {
  def getAllLetters: UIO[String]
}

object LettersService {
  val live: ULayer[LettersService] = ZLayer.succeed {
    new LettersService {
      override def getAllLetters: UIO[String] = ZIO.succeed("All the letters")
    }
  }
}
