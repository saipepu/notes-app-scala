package users

import zio.interop.catz._
import doobie.implicits._
import doobie.hikari.HikariTransactor
import zio.{Task, ZLayer}

case class User(id: Long, name: String, email: String)
trait UsersService {
  def getAllUsers: Task[List[User]]
  def getUserById(id: Long): Task[Option[User]]
  def createUser(name: String, email: String): Task[User]
  def deleteUser(id: Long): Task[Int]
}

class UsersServiceImpl(xa: HikariTransactor[Task]) extends UsersService {

  override def getAllUsers: Task[List[User]] = {
    val query = sql"SELECT id, name, email FROM users"
      .query[User]
      .to[List]

    query.transact(xa)
  }

  override def getUserById(id: Long): Task[Option[User]] = {
    val query = sql"SELECT id, name, email FROM users WHERE id = $id"
      .query[User]
      .option

    query.transact(xa)
  }

  override def createUser(name: String, email: String): Task[User] = {
    val insert = sql"INSERT INTO users (name, email) VALUES ($name, $email)"
      .update
      .withUniqueGeneratedKeys[Long]("id")

    insert.transact(xa).map(id => User(id, name, email))
  }

  override def deleteUser(id: Long): Task[Int] = {
    val delete = sql"DELETE FROM users WHERE id = $id".update.run
    delete.transact(xa)
  }

}

object UsersService {
  val live: ZLayer[HikariTransactor[Task], Nothing, UsersService] = {
//    ZLayer.fromFunction(new UsersServiceImpl(_))
    ZLayer.fromFunction((xa: HikariTransactor[Task]) => new UsersServiceImpl(xa))
  }
}