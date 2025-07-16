import HightlightedText.{HightlightedTextRoutes, HightlightedTextService}
import letters.{LettersRoutes, LettersService}
import notes.{NotesRoutes, NotesService}
import users.{UsersRoutes, UsersService}
import zio._
import zio.http._
import doobie.hikari.HikariTransactor

object Main extends ZIOAppDefault {
  private type Envs = LettersService with NotesService with UsersService with HightlightedTextService with HikariTransactor[Task]
  private val fullLayer = ZLayer.make[Envs](
    UsersService.live,
    NotesService.live,
    LettersService.live,
    HightlightedTextService.live,
    Database.layer,
  )

  val appLogic: ZIO[Envs, Any, Any] =
    for {
      _ <- ZIO.logInfo("Starting application...")
      _ <- DatabaseMigration.createTables
      _ <- ZIO.logInfo("Database migration completed")
      routesList <- ZIO.collectAll(List(
        NotesRoutes.make,
        UsersRoutes.make,
        LettersRoutes.make,
        HightlightedTextRoutes.make
      ))
      combinedRoutes = routesList.reduce(_ ++ _)
      httpApp = combinedRoutes.toHttpApp
      _ <- ZIO.logInfo("Server starting on port 8080...")
      _ <- Server.serve(httpApp).provide(Server.default)
    } yield ()

  override val run: ZIO[Any, Any, Any] = appLogic.provide(fullLayer)
}