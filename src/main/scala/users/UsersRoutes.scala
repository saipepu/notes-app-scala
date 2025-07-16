package users

import zio._
import zio.http._
import zio.json._

class UsersRoutes(service: UsersService) {

  private def mightFailFunction(): Option[String] = {
    if(scala.util.Random.nextBoolean()) {
      Some("success")
    } else {
      None
    }
  }

  implicit val userEncoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
  implicit val userDecoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]

  case class CreateUserRequest(name: String, email: String)
  implicit val createUserRequestDecoder: JsonDecoder[CreateUserRequest] = DeriveJsonDecoder.gen[CreateUserRequest]

  val routes: Routes[Any, Response] = Routes(
    // GET /users
    Method.GET / "users" -> Handler.fromZIO(
      for {
        _     <- ZIO.logInfo("Fetching all users")
        users <- service.getAllUsers
        _     <- ZIO.logInfo("Fetched all users")
      } yield Response.json(users.toJson)
    ).catchAll(error =>
      Handler.succeed(
        Response
          .status(Status.InternalServerError)
          .copy(body = Body.fromString(s"Error: ${error.getMessage}"))
      )
    ),

    Method.POST / "users" -> Handler.fromFunctionZIO { (request: Request) =>
      (for {
        _ <- ZIO.logInfo("Creating new user")
        body <- request.body.asString
        createUserRequest <- ZIO.fromEither(body.fromJson[CreateUserRequest])
          .mapError(error => new RuntimeException(s"Invalid JSON: $error"))
        user <- service.createUser(createUserRequest.name, createUserRequest.email)
        _ <- ZIO.logInfo(s"Created user with id: ${user.id}")
      } yield Response.json(user.toJson)).catchAll(error =>
        ZIO.succeed(
          Response
            .status(Status.InternalServerError)
            .copy(body = Body.fromString(s"Error: ${error.getMessage}"))
        )
      )
    },

    Method.GET / "users" / long("id") -> handler { (id: Long, _: Request) =>
      for {
        _ <- ZIO.logInfo(s"Fetching user by $id")
        userOpt <- service.getUserById(id)
        response <- userOpt match {
          case Some(user) => ZIO.succeed(Response.json(user.toJson))
          case None => ZIO.succeed(Response.status(Status.NotFound)
            .copy(body = Body.fromString(s"""{"error": "User not found"}""")))
        }
      } yield response
    }.catchAll(error =>
      Handler.succeed(
        Response
          .status(Status.InternalServerError)
          .copy(body = Body.fromString(s"Error: ${error.getMessage}"))
      )
    ),

    Method.DELETE / "users" / long("id") -> handler { (id: Long, _: Request) =>
      for {
        _ <- ZIO.logInfo(s"Deleting user with id: $id")
        deletedOpt <- service.deleteUser(id)
        response <- deletedOpt match {
          case 1 => ZIO.succeed(Response.text(s"User with id: $id was deleted successfully"))
          case 0 => ZIO.succeed(Response.status(Status.NotFound)
            .copy(body = Body.fromString(s"""{"error": "User not found"}""")))
        }
      } yield response
    }.catchAll(error =>
      Handler.succeed(
        Response
          .status(Status.InternalServerError)
          .copy(body = Body.fromString(s"Error: ${error.getMessage}"))
      )
    ),

    Method.GET / "users" / string("name") -> handler { (name: String, _: Request) =>
      for {
        _ <- ZIO.logInfo(s"Finding user with name: $name")
        resultOpt <- ZIO.attempt(Option(mightFailFunction()).flatten)
        response <- resultOpt match {
          case Some(result) => ZIO.succeed(Response.text(s"Found: $result"))
          case None => ZIO.succeed(Response.status(Status.NotFound)
            .copy(body = Body.fromString(s"""{"error": "User not found or operation failed"}""")))
        }
      } yield response
    }.catchAll(error =>
      Handler.succeed(
        Response
          .status(Status.InternalServerError)
          .copy(body = Body.fromString(s"Error:"))
      )
    ),

  )
}

object UsersRoutes {
  def make: URIO[UsersService, Routes[Any, Response]] = {
    ZIO.serviceWith[UsersService] (
      svc => new UsersRoutes(svc).routes
    )
  }
}