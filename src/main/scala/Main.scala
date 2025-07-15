import HightlightedText.{HightlightedTextRoutes, HightlightedTextService}
import letters.{LettersRoutes, LettersService}
import notes.{NotesRoutes, NotesService}
import users.{UsersRoutes, UsersService}
import zio._
import zio.http._

object Main extends ZIOAppDefault {
  type Envs = LettersService with NotesService with UsersService with HightlightedTextService
  val fullLayer = ZLayer.make[Envs](
    UsersService.live,
    NotesService.live,
    LettersService.live,
    HightlightedTextService.live,
  )

  val appLogic: ZIO[Envs, Any, Any] =
    for {
      routesList <- ZIO.collectAll(List(
        NotesRoutes.make,
        UsersRoutes.make,
        LettersRoutes.make,
        HightlightedTextRoutes.make
      ))
      combinedRoutes = routesList.reduce(_ ++ _)
      httpApp = combinedRoutes.toHttpApp
      _ <- Server.serve(httpApp).provide(Server.default)
    } yield ()

  override val run: ZIO[Any, Any, Any] = appLogic.provide(fullLayer)
}