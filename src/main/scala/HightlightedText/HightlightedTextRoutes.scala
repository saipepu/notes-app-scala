package HightlightedText

import zio.{URIO, ZIO}
import zio.http._

class HightlightedTextRoutes(service: HightlightedTextService) {
  private val routes: Routes[Any, Nothing] = Routes(
    Method.GET / "hightlighted-text" -> Handler.fromZIO(
      for {
        _ <- ZIO.logInfo("Fetching all the hightlighted text")
        hightlighted_text <- service.getAllHightlightedText
      } yield Response.text(hightlighted_text)
    )
  )
}

object HightlightedTextRoutes {
  val make: URIO[HightlightedTextService, Routes[Any, Nothing]] =
    ZIO.serviceWith[HightlightedTextService] (
      srv => new HightlightedTextRoutes(srv).routes
    )
}
