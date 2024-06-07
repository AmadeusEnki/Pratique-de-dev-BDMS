package ch.hearc.cafheg.infrastructure.persistance;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestion des scripts de migration sur la base de données.
 */
public class Migrations {

  private static final Logger logger = LoggerFactory.getLogger(Migrations.class);
  private final Database database;
  private final boolean forTest;

  public Migrations(Database database) {
    this.database = database;
    this.forTest = false;
  }

  /**
   * Exécution des migrations
   */
  public void start() {
    logger.info("Début de l'exécution des migrations");

    String location;
    // Pour les tests, on éxécute que les scripts DDL (création de tables)
    // et pas les scripts d'insertion de données.
    if (forTest) {
      location = "classpath:db/ddl";
      logger.debug("Exécution en mode test, location: {}", location);
    } else {
      location = "classpath:db";
      logger.debug("Exécution en mode production, location: {}", location);
    }

    Flyway flyway = Flyway.configure()
            .dataSource(database.dataSource())
            .locations(location)
            .load();

    flyway.migrate();
    logger.info("Migrations terminées avec succès");
  }
}
