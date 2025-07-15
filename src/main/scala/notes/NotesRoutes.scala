package notes

import zio.{URIO, ZIO}
import zio.http._

class NotesRoutes(service: NotesService) {
  val routes: Routes[Any, Nothing] = Routes (
    Method.GET / "notes" -> Handler.fromZIO(
      for {
        _ <- ZIO.logInfo("Fetching all the notes")
        notes <- service.getAllNotes
      } yield Response.text(notes)
    )
  )
}

object NotesRoutes {
  def make: URIO[NotesService, Routes[Any, Nothing]] =
    ZIO.serviceWith[NotesService](
      svc => new NotesRoutes(svc).routes
    )
}