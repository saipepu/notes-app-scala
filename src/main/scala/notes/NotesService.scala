package notes

import zio.{UIO, ULayer, ZIO, ZLayer}

trait NotesService {
  def getAllNotes: UIO[String]
}

object NotesService {
  val live: ULayer[NotesService] = ZLayer.succeed(
    new NotesService {
      def getAllNotes: UIO[String] = ZIO.succeed("All the notes")
    }
  )
}
