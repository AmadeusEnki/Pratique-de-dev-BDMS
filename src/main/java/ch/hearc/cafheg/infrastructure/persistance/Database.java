package ch.hearc.cafheg.infrastructure.persistance;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
  private static final Logger logger = LoggerFactory.getLogger(Database.class);

  /** Pool de connections JDBC */
  private static HikariDataSource dataSource;

  /** Connection JDBC active par utilisateur/thread (ThreadLocal) */
  private static final ThreadLocal<Connection> connection = new ThreadLocal<>();

  /**
   * Retourne la transaction active ou throw une Exception si pas de transaction
   * active.
   * @return Connection JDBC active
   */
  public static Connection activeJDBCConnection() {
    if (connection.get() == null) {
      throw new RuntimeException("Pas de connection JDBC active");
    }
    return connection.get();
  }

  /**
   * Exécution d'une fonction dans une transaction.
   * @param inTransaction La fonction a éxécuter au travers d'une transaction
   * @param <T> Le type du retour de la fonction
   * @return Le résultat de l'éxécution de la fonction
   */
  public static <T> T inTransaction(Supplier<T> inTransaction) {
    logger.debug("Début de la transaction");
    try {
      logger.debug("Obtention de la connection");
      connection.set(dataSource.getConnection());
      return inTransaction.get();
    } catch (Exception e) {
      logger.error("Erreur pendant l'exécution de la transaction", e);
      throw new RuntimeException(e);
    } finally {
      try {
        logger.debug("Fermeture de la connection");
        connection.get().close();
      } catch (SQLException e) {
        logger.error("Erreur lors de la fermeture de la connection", e);
        throw new RuntimeException(e);
      }
      logger.debug("Fin de la transaction");
      connection.remove();
    }
  }

  public static DataSource dataSource() {
    return dataSource;
  }

  /**
   * Initialisation du pool de connections.
   */
  public void start() {
    logger.info("Initialisation du pool de connections");
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:h2:mem:sample");
    config.setMaximumPoolSize(20);
    config.setDriverClassName("org.h2.Driver");
    dataSource = new HikariDataSource(config);
    logger.info("Pool de connections initialisé");
  }

  /**
   * Fermeture du pool de connections.
   */
  public void stop() {
    if (dataSource != null) {
      dataSource.close();
      logger.info("Pool de connections fermé");
    }
  }

  /**
   * Initialisation explicite de la connexion JDBC.
   */
  public static void initConnection() throws SQLException {
    connection.set(dataSource.getConnection());
  }
}
