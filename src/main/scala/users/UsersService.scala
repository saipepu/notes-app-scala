package users

import zio.{Task, UIO, ULayer, ZIO, ZLayer}

case class User(id: Int, name: String, email: String)

trait UsersService {
  def getAllUsers: UIO[String]
}

object UsersService {
  val live: ULayer[UsersService] = ZLayer.succeed {
    new UsersService{
      override def getAllUsers: UIO[String] = ZIO.succeed("All the users")
    }
  }
}