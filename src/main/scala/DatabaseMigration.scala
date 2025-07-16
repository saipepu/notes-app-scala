import doobie._
import doobie.implicits._
import doobie.hikari.HikariTransactor
import zio._
import zio.interop.catz._

object DatabaseMigration {

  def createTables: ZIO[HikariTransactor[Task], Throwable, Unit] = {
    val createUsersTable = sql"""
      CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        email VARCHAR(255) NOT NULL UNIQUE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    """.update.run

    val createNotesTable = sql"""
      CREATE TABLE IF NOT EXISTS notes (
        id SERIAL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        content TEXT NOT NULL,
        user_id INTEGER REFERENCES users(id),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    """.update.run

    val createLettersTable = sql"""
      CREATE TABLE IF NOT EXISTS letters (
        id SERIAL PRIMARY KEY,
        subject VARCHAR(255) NOT NULL,
        body TEXT NOT NULL,
        recipient_email VARCHAR(255) NOT NULL,
        user_id INTEGER REFERENCES users(id),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    """.update.run

    val createHighlightedTextTable = sql"""
      CREATE TABLE IF NOT EXISTS highlighted_text (
        id SERIAL PRIMARY KEY,
        text TEXT NOT NULL,
        note_id INTEGER REFERENCES notes(id),
        user_id INTEGER REFERENCES users(id),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )
    """.update.run

    for {
      xa <- ZIO.service[HikariTransactor[Task]]
      _ <- createUsersTable.transact(xa)
      _ <- createNotesTable.transact(xa)
      _ <- createLettersTable.transact(xa)
      _ <- createHighlightedTextTable.transact(xa)
      _ <- ZIO.logInfo("Database tables created successfully")
    } yield ()
  }
}