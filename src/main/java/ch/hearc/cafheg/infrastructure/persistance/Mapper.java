package ch.hearc.cafheg.infrastructure.persistance;

import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe abstraite permettant à chaque implémentation de Mapper
 * de récupérer la connection JDBC active.
 */
public abstract class Mapper {

  private static final Logger logger = LoggerFactory.getLogger(Mapper.class);

  protected Connection activeJDBCConnection() {
    Connection connection = Database.activeJDBCConnection();
    logger.debug("Obtention de la connexion JDBC active : {}", connection);
    return connection;
  }
}
