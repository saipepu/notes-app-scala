package users

import zio._
import zio.http._
import zio.http.codec.PathCodec

class UsersRoutes(service: UsersService) {

  val routes: Routes[Any, Nothing] = Routes(
    // GET /users
    Method.GET / "users" -> Handler.fromZIO(
      for {
        _     <- ZIO.logInfo("Fetching all users")
        users <- service.getAllUsers
        _     <- ZIO.logInfo("Fetched all users")
      } yield Response.text(users)
    ),

    // GET /users/:id
    Method.GET / "user" / long("id") -> handler { (id: Long, req: Request) =>
      for {
        _ <- ZIO.logInfo(s"Fetching user by $id")
      } yield Response.text(s"user by $id")
    }
  )
}

object UsersRoutes {
  def make: URIO[UsersService, Routes[Any, Nothing]] = {
    ZIO.serviceWith[UsersService] (
      svc => new UsersRoutes(svc).routes
    )
  }
}