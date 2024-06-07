package ch.hearc.cafheg.infrastructure.persistance;

import ch.hearc.cafheg.business.allocations.Allocataire;
import ch.hearc.cafheg.business.allocations.NoAVS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllocataireMapper extends Mapper {

  private static final Logger logger = LoggerFactory.getLogger(AllocataireMapper.class);

  // Requêtes SQL préparées
  private static final String QUERY_FIND_ALL = "SELECT NOM, PRENOM, NO_AVS FROM ALLOCATAIRES";
  private static final String QUERY_FIND_WHERE_NOM_LIKE = "SELECT NOM, PRENOM, NO_AVS FROM ALLOCATAIRES WHERE NOM LIKE ?";
  private static final String QUERY_FIND_WHERE_NUMERO = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_DELETE_WHERE_NUMERO = "DELETE FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_UPDATE_WHERE_NUMERO = "UPDATE ALLOCATAIRES SET NOM=?, PRENOM=? WHERE NUMERO=?";
  private static final String QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO = "SELECT * FROM VERSEMENTS WHERE NUMERO=?";

  // Méthode pour trouver tous les allocataires selon un nom similaire
  public List<Allocataire> findAll(String likeNom) {
    logger.debug("Recherche de tous les allocataires avec un nom commençant par : {}", likeNom);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement;
      if (likeNom == null) {
        logger.debug("SQL: {}", QUERY_FIND_ALL);
        preparedStatement = connection.prepareStatement(QUERY_FIND_ALL);
      } else {
        logger.debug("SQL: {}", QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement.setString(1, likeNom + "%");
      }
      List<Allocataire> allocataires = new ArrayList<>();
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          allocataires.add(new Allocataire(new NoAVS(resultSet.getString(3)), resultSet.getString(2), resultSet.getString(1)));
        }
      }
      logger.info("Nombre d'allocataires trouvés : {}", allocataires.size());
      return allocataires;
    } catch (SQLException e) {
      logger.error("Erreur lors de la recherche des allocataires", e);
      throw new RuntimeException(e);
    }
  }

  // Méthode pour trouver un allocataire par son ID
  public Allocataire findById(long id) {
    logger.debug("Recherche de l'allocataire avec l'ID : {}", id);
    Connection connection = activeJDBCConnection();
    try {
      logger.debug("SQL: {}", QUERY_FIND_WHERE_NUMERO);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO);
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();
      return new Allocataire(new NoAVS(resultSet.getString(1)), resultSet.getString(2), resultSet.getString(3));
    } catch (SQLException e) {
      logger.error("Erreur lors de la recherche de l'allocataire", e);
      throw new RuntimeException(e);
    }
  }

  // Méthode pour supprimer un allocataire par son ID
  public String delete(int id) {
    logger.debug("Début de la suppression de l'allocataire : {}", id);
    Connection connection = activeJDBCConnection();
    try {
      logger.debug("Vérification si l'allocataire {} a des versements liés", id);
      logger.debug("SQL: {}", QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO);
      PreparedStatement checkVersementStatement = connection.prepareStatement(QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO);
      checkVersementStatement.setLong(1, id);
      ResultSet resultSetVersement = checkVersementStatement.executeQuery();

      if (resultSetVersement.next()) {
        logger.warn("L'allocataire {} a des versements liés, impossible de le supprimer", id);
        return "L'allocataire " + id + " a des versements liés, impossible de le supprimer.";
      } else {
        logger.debug("SQL: {}", QUERY_DELETE_WHERE_NUMERO);
        PreparedStatement deleteStatement = connection.prepareStatement(QUERY_DELETE_WHERE_NUMERO);
        deleteStatement.setLong(1, id);
        int check = deleteStatement.executeUpdate();
        if (check == 0) {
          logger.warn("Allocataire {} non trouvé", id);
          return "Allocataire " + id + " non trouvé";
        }
      }
      logger.info("Allocataire {} supprimé", id);
      return "Allocataire " + id + " supprimé";
    } catch (SQLException e) {
      logger.error("Erreur lors de la suppression de l'allocataire", e);
      throw new RuntimeException(e);
    }
  }

  // Méthode pour mettre à jour un allocataire avec un nouveau nom et prénom
  public String update(Allocataire allocataire) {
    NoAVS noAVS = allocataire.getNoAVS(); // Obtenir le numéro AVS de l'allocataire
    String id = noAVS.getValue(); // Obtenir l'ID de l'allocataire
    String nom = allocataire.getNom(); // Obtenir le nouveau nom de l'allocataire
    String prenom = allocataire.getPrenom(); // Obtenir le nouveau prénom de l'allocataire

    logger.info("Début de la mise à jour de l'allocataire {} avec un nouveau nom et prénom", id);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement checkStatement = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO);
      checkStatement.setString(1, id);
      ResultSet resultSet = checkStatement.executeQuery();
      resultSet.next();

      // Vérifier si le nom et le prénom sont identiques à ceux existants
      if (resultSet.getString(2).equals(nom) && resultSet.getString(3).equals(prenom)) {
        logger.info("Nom et prénom identiques trouvés : {} {}, aucune mise à jour nécessaire", nom, prenom);
        return "Le nom et le prénom de l'allocataire " + id + " sont identiques, aucune mise à jour nécessaire";
      }

      logger.info("Le nom et le prénom de l'allocataire {} sont différents, mise à jour en cours", id);
      PreparedStatement updateStatement = connection.prepareStatement(QUERY_UPDATE_WHERE_NUMERO);
      updateStatement.setString(1, nom);
      updateStatement.setString(2, prenom);
      updateStatement.setString(3, id);
      int check = updateStatement.executeUpdate();

      // Vérifier si l'allocataire a été trouvé et mis à jour
      if (check == 0) {
        logger.warn("Allocataire {} non trouvé", id);
        return "Allocataire " + id + " non trouvé";
      }

      logger.info("Allocataire {} mis à jour avec succès", id);
      return "Allocataire " + id + " mis à jour";
    } catch (SQLException e) {
      logger.error("Erreur lors de la mise à jour de l'allocataire", e);
      throw new RuntimeException(e);
    }
  }
}
