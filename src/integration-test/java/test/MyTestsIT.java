package test;

import ch.hearc.cafheg.business.allocations.*;
import ch.hearc.cafheg.infrastructure.persistance.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class MyTestsIT {

    private AllocataireService allocataireService;
    private AllocationService allocationService;
    private DataSource dataSource;

    @BeforeEach
    public void setUp() throws Exception {
        IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(
                ClassLoader.getSystemResourceAsStream("dataSet.xml"));
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
    }

    @Test
    public void testDeleteAllocataire() {
        String noAVS = "123456789";
        boolean result = allocataireService.deleteAllocataire(noAVS);

        assertThat(result).isTrue();
    }

    @Test
    public void testUpdateAllocataire() {
        NoAVS noAVS = new NoAVS("987654321");
        String nouveauNom = "DoeUpdated";
        String nouveauPrenom = "JaneUpdated";

        Allocataire updatedAllocataire = allocataireService.updateAllocataire(noAVS, nouveauNom, nouveauPrenom);

        assertThat(updatedAllocataire.getNom()).isEqualTo(nouveauNom);
        assertThat(updatedAllocataire.getPrenom()).isEqualTo(nouveauPrenom);
    }
}
