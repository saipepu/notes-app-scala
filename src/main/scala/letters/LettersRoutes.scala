package letters

import zio.{URIO, ZIO}
import zio.http._

private class LettersRoutes(service: LettersService) {
  private val routes: Routes[Any, Nothing] = Routes(
    Method.GET / "letters" -> Handler.fromZIO (
      for {
        _ <- ZIO.logInfo("Fetching all users")
        letters <- service.getAllLetters
      } yield Response.text(letters)
    )
  )
}

object LettersRoutes {
  def make: URIO[LettersService, Routes[Any, Nothing]] =
    ZIO.serviceWith[LettersService] (
      svc => new LettersRoutes(svc).routes
    )
}
