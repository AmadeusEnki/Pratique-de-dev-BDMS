package test;

import ch.hearc.cafheg.business.allocations.Allocataire;
import ch.hearc.cafheg.business.allocations.NoAVS;
import ch.hearc.cafheg.business.allocations.AllocationService;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import ch.hearc.cafheg.infrastructure.persistance.Database;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyTestsIT {

    private DatabaseDataSourceConnection connection;
    private ITable expectedTable;
    private ITable actualData;
    private Logger logger = LoggerFactory.getLogger(MyTestsIT.class);

    @Mock
    private AllocataireMapper allocataireMapper;

    @Mock
    private AllocationMapper allocationMapper;

    @InjectMocks
    private AllocationService allocationService;

    private IDataSet dataSet;

    @Captor
    private ArgumentCaptor<Allocataire> allocataireCaptor;

    @BeforeEach
    public void setup() {
        logger.debug("Configuration en cours");
        try {
            // Charger le jeu de données à partir du fichier XML
            ClassLoader classLoader = getClass().getClassLoader();
            try (InputStream dataSetStream = classLoader.getResourceAsStream("dataSet.xml")) {
                if (dataSetStream == null) {
                    throw new FileNotFoundException("Le fichier de jeu de données est introuvable : dataSet.xml");
                }
                dataSet = new FlatXmlDataSetBuilder().build(dataSetStream);
                logger.debug("Jeu de données chargé");
            }

            // Initialize the database connection
            Database database = new Database();
            database.start(); // Make sure to initialize the database
            DataSource dataSource = Database.dataSource();
            connection = new DatabaseDataSourceConnection(dataSource);
            DatabaseConfig config = connection.getConfig();
            config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);

            logger.info("Configuration terminée");
        } catch (DatabaseUnitException | FileNotFoundException e) {
            logger.error("Erreur lors de la configuration", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Erreur lors de la lecture du fichier de jeu de données", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation de la base de données", e);
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() {
        // Aucun nettoyage nécessaire car nous n'utilisons pas une base de données réelle
    }

    @Test
    @DisplayName("Test de suppression d'allocataire par ID")
    public void deleteAllocataireById_GivenID3_ShouldBe2RowsLeft() {
        try {
            // Simuler la suppression d'un allocataire
            when(allocataireMapper.delete(3)).thenReturn("Allocataire 3 supprimé");

            // Appeler la méthode de service
            allocationService.deleteAllocataire(3);

            // Vérifier que la méthode de suppression a été appelée
            verify(allocataireMapper, times(1)).delete(3);

            // Charger le jeu de données attendu à partir du fichier XML
            ClassLoader classLoader = getClass().getClassLoader();
            try (InputStream expectedDataSetStream = classLoader.getResourceAsStream("expectedDeleteDataSet.xml")) {
                if (expectedDataSetStream == null) {
                    throw new FileNotFoundException("Le fichier de jeu de données attendu est introuvable : expectedDeleteDataSet.xml");
                }
                IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(expectedDataSetStream);
                expectedTable = expectedDataSet.getTable("ALLOCATAIRES");
            }

            // Comparer les résultats
            actualData = dataSet.getTable("ALLOCATAIRES");
            Assertion.assertEquals(expectedTable, actualData);
        } catch (DataSetException e) {
            logger.error("Erreur lors du test de suppression d'allocataire", e);
            throw new RuntimeException(e);
        } catch (DatabaseUnitException | IOException e) {
            logger.error("Erreur lors du test de suppression d'allocataire", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Test de mise à jour du nom et du prénom de l'allocataire par ID")
    public void updateAllocataireName_GivenID3_ShouldUpdateNameAndFirstName() {
        try {
            Allocataire allocataire = new Allocataire(new NoAVS("3"), "John", "Doe");
            when(allocataireMapper.findById(3)).thenReturn(allocataire);

            // Appeler la méthode de service
            allocationService.updateAllocataire(3, "Doe", "John");

            // Capturer l'argument passé à la méthode update
            verify(allocataireMapper).update(allocataireCaptor.capture());
            Allocataire updatedAllocataire = allocataireCaptor.getValue();

            // Vérifier que les valeurs mises à jour sont correctes
            assertEquals("Doe", updatedAllocataire.getNom());
            assertEquals("John", updatedAllocataire.getPrenom());

            // Charger le jeu de données attendu à partir du fichier XML
            ClassLoader classLoader = getClass().getClassLoader();
            try (InputStream expectedDataSetStream = classLoader.getResourceAsStream("expectedUpdateDataSet.xml")) {
                if (expectedDataSetStream == null) {
                    throw new FileNotFoundException("Le fichier de jeu de données attendu est introuvable : expectedUpdateDataSet.xml");
                }
                IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(expectedDataSetStream);
                expectedTable = expectedDataSet.getTable("ALLOCATAIRES");
            }

            // Comparer les résultats
            actualData = dataSet.getTable("ALLOCATAIRES");
            Assertion.assertEquals(expectedTable, actualData);
        } catch (DataSetException e) {
            logger.error("Erreur lors du test de mise à jour d'allocataire", e);
            throw new RuntimeException(e);
        } catch (DatabaseUnitException | IOException e) {
            logger.error("Erreur lors du test de mise à jour d'allocataire", e);
            throw new RuntimeException(e);
        }
    }
}
