package ch.hearc.cafheg.infrastructure.persistance;

import ch.hearc.cafheg.business.allocations.Allocation;
import ch.hearc.cafheg.business.allocations.Canton;
import ch.hearc.cafheg.business.common.Montant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllocationMapper extends Mapper {

  private static final Logger logger = LoggerFactory.getLogger(AllocationMapper.class);

  // Requête SQL préparée
  private static final String QUERY_FIND_ALL = "SELECT * FROM ALLOCATIONS";

  // Méthode pour trouver toutes les allocations
  public List<Allocation> findAll() {
    logger.debug("Recherche de toutes les allocations");

    Connection connection = activeJDBCConnection();
    try {
      logger.debug("SQL: {}", QUERY_FIND_ALL);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_ALL);
      ResultSet resultSet = preparedStatement.executeQuery();
      List<Allocation> allocations = new ArrayList<>();
      while (resultSet.next()) {
        logger.debug("Traitement de la ligne suivante du ResultSet");
        allocations.add(
                new Allocation(
                        new Montant(resultSet.getBigDecimal(2)),
                        Canton.fromValue(resultSet.getString(3)),
                        resultSet.getDate(4).toLocalDate(),
                        resultSet.getDate(5) != null ? resultSet.getDate(5).toLocalDate() : null
                )
        );
      }
      logger.info("Nombre d'allocations trouvées : {}", allocations.size());
      return allocations;
    } catch (SQLException e) {
      logger.error("Erreur lors de la recherche des allocations", e);
      throw new RuntimeException(e);
    }
  }
}
