import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import zio.TaskLayer
import zio._
import doobie.util.ExecutionContexts

object Database {
  val layer: TaskLayer[HikariTransactor[Task]] =
    ZLayer.scoped {
      for {
        ec <- ZIO.executor.map(_.asExecutionContext)
        cfg = {
          val c = new HikariConfig()
          c.setJdbcUrl("jdbc:postgresql://localhost:5432/notes")
          c.setUsername("myuser")
          c.setPassword("mypassword")
          c
        }
        ds = new HikariDataSource(cfg)
        xa <- HikariTransactor.newHikariTransactor[Task](
          driverClassName = "org.postgresql.Driver",
          url = cfg.getJdbcUrl,
          user = cfg.getUsername,
          pass = cfg.getPassword,
          connectEC = ec
        )
      } yield xa
    }
}
