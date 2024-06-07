package ch.hearc.cafheg.infrastructure.persistance;

import ch.hearc.cafheg.business.allocations.Allocataire;
import ch.hearc.cafheg.business.allocations.NoAVS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AllocataireMapper extends Mapper {

  private static final String QUERY_FIND_ALL = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES";
  private static final String QUERY_FIND_WHERE_NOM_LIKE = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES WHERE NOM LIKE ?";
  private static final String QUERY_FIND_WHERE_NUMERO = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String INSERT_ALLOCATAIRE = "INSERT INTO ALLOCATAIRES (NO_AVS, NOM, PRENOM) VALUES (?, ?, ?)";
  private static final String UPDATE_ALLOCATAIRE = "UPDATE ALLOCATAIRES SET NOM=?, PRENOM=? WHERE NO_AVS=?";
  private static final String DELETE_ALLOCATAIRE = "DELETE FROM ALLOCATAIRES WHERE NO_AVS = ?";

  public List<Allocataire> findAll(String likeNom) {
    System.out.println("findAll() " + likeNom);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement;
      if (likeNom == null) {
        System.out.println("SQL: " + QUERY_FIND_ALL);
        preparedStatement = connection
            .prepareStatement(QUERY_FIND_ALL);
      } else {

        System.out.println("SQL: " + QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement = connection
            .prepareStatement(QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement.setString(1, likeNom + "%");
      }
      System.out.println("Allocation d'un nouveau tableau");
      List<Allocataire> allocataires = new ArrayList<>();

      System.out.println("Exécution de la requête");
      try (ResultSet resultSet = preparedStatement.executeQuery()) {

        System.out.println("Allocataire mapping");
        while (resultSet.next()) {
          System.out.println("ResultSet#next");
          allocataires
              .add(new Allocataire(new NoAVS(resultSet.getString(3)), resultSet.getString(2),
                  resultSet.getString(1)));
        }
      }
      System.out.println("Allocataires trouvés " + allocataires.size());
      return allocataires;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Allocataire findById(long id) {
    System.out.println("findById() " + id);
    Connection connection = activeJDBCConnection();
    try {
      System.out.println("SQL:" + QUERY_FIND_WHERE_NUMERO);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO);
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      System.out.println("ResultSet#next");
      resultSet.next();
      System.out.println("Allocataire mapping");
      return new Allocataire(new NoAVS(resultSet.getString(1)),
          resultSet.getString(2), resultSet.getString(3));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void insert(Allocataire allocataire) {
    try (Connection connection = activeJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(INSERT_ALLOCATAIRE)) {
      preparedStatement.setString(1, allocataire.getNoAVS().toString());
      preparedStatement.setString(2, allocataire.getNom());
      preparedStatement.setString(3, allocataire.getPrenom());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void update(Allocataire allocataire) {
    try (Connection connection = activeJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ALLOCATAIRE)) {
      preparedStatement.setString(1, allocataire.getNom());
      preparedStatement.setString(2, allocataire.getPrenom());
      preparedStatement.setString(3, allocataire.getNoAVS().toString());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(NoAVS noAVS) {
    try (Connection connection = activeJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALLOCATAIRE)) {
      preparedStatement.setString(1, noAVS.toString());
      int affectedRows = preparedStatement.executeUpdate();
      if (affectedRows == 0) {
        throw new SQLException("La suppression de l'allocataire a échoué, aucun ligne affectée.");
      }
    } catch (SQLException e) {
      throw new RuntimeException("Erreur lors de la suppression de l'allocataire", e);
    }
  }
}
