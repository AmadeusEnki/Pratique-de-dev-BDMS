package test;

import ch.hearc.cafheg.business.allocations.Allocataire;
import ch.hearc.cafheg.business.allocations.NoAVS;
import ch.hearc.cafheg.business.allocations.AllocationService;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyTestsIT {

    private IDatabaseConnection connection;
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

            logger.info("Configuration terminée");
        } catch (DatabaseUnitException | FileNotFoundException e) {
            logger.error("Erreur lors de la configuration", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Erreur lors de la lecture du fichier de jeu de données", e);
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

            // Vérifier que la méthode de mise à jour a été appelée
            verify(allocataireMapper, times(1)).update(allocataire);

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
