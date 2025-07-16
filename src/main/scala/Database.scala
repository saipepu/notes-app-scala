import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import doobie.hikari.HikariTransactor
import zio._
import zio.interop.catz._
import doobie.util.ExecutionContexts

object Database {
  val layer: TaskLayer[HikariTransactor[Task]] =
    ZLayer.scoped {
      for {
        ec  <- ZIO.executor.map(_.asExecutionContext)
        cfg  = {
          val c = new HikariConfig()
          c.setJdbcUrl("jdbc:postgresql://localhost:5432/notes")
          c.setUsername("myuser")
          c.setPassword("mypassword")
          c
        }
        ds  = new HikariDataSource(cfg)
        xa <- ZIO.fromAutoCloseable(ZIO.attempt(new HikariDataSource(cfg)))
          .map(ds => HikariTransactor.apply[Task](ds, ec))
      } yield xa
    }
}