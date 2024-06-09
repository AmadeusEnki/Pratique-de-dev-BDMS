package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.business.versements.VersementService;
import ch.hearc.cafheg.infrastructure.pdf.PDFExporter;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import ch.hearc.cafheg.infrastructure.persistance.VersementMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AllocationServiceTest {

  private AllocationService allocationService;

    private Famille famille;

  @BeforeEach
  void setUp() {
      AllocataireMapper allocataireMapper = Mockito.mock(AllocataireMapper.class);
      AllocationMapper allocationMapper = Mockito.mock(AllocationMapper.class);
      VersementMapper versementMapper = Mockito.mock(VersementMapper.class);
      PDFExporter pdfExporter = Mockito.mock(PDFExporter.class);
      VersementService versementService = new VersementService(versementMapper, allocataireMapper, pdfExporter);

    allocationService = new AllocationService(allocataireMapper, allocationMapper, versementService);

    // Initialize the famille object with default values
    famille = new Famille(
            true, // parent1ActiviteLucrative
            true, // parent2ActiviteLucrative
            true, // parent1AutoriteParentale
            true, // parent2AutoriteParentale
            true, // parentsViventEnsemble
            true, // parent1TravailDansCantonDomicileEnfant
            true, // parent2TravailDansCantonDomicileEnfant
            true, // parent1EstSalarie
            true, // parent2EstSalarie
            false, // parent1EstIndependant
            false, // parent2EstIndependant
            new BigDecimal(1000), // parent1Salaire
            new BigDecimal(1000), // parent2Salaire
            "P1Residence", // parent1Residence
            "P2Residence", // parent2Residence
            "P1Residence" // enfantResidence
    );
  }

  @Test
  @DisplayName("Scénario a : Si un seul parent est actif, ce parent a droit à l'allocation (Parent 1 actif)")
  void givenParent1Active_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent1() {
    famille.setParent2ActiviteLucrative(false);

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent1", result);
  }

  @Test
  @DisplayName("Scénario a : Si un seul parent est actif, ce parent a droit à l'allocation (Parent 2 actif)")
  void givenParent2Active_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent2() {
    famille.setParent1ActiviteLucrative(false);

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent2", result);
  }

  @Test
  @DisplayName("Scénario b : Si un parent a l'autorité parentale, ce parent a droit à l'allocation (Parent 1 avec autorité parentale)")
  void givenParent1WithParentalAuthority_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent1() {
    famille.setParent2AutoriteParentale(false);

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent1", result);
  }

  @Test
  @DisplayName("Scénario b : Si un parent a l'autorité parentale, ce parent a droit à l'allocation (Parent 2 avec autorité parentale)")
  void givenParent2WithParentalAuthority_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent2() {
    famille.setParent1AutoriteParentale(false);

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent2", result);
  }

  @Test
  @DisplayName("Scénario c : Si les parents sont séparés, le parent avec qui l'enfant vit a droit à l'allocation (Enfant vivant avec Parent 1)")
  void givenParentsSeparatedAndChildLivesWithParent1_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent1() {
    famille.setParentsViventEnsemble(false);
    famille.setEnfantResidence(famille.getParent1Residence());

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent1", result);
  }

  @Test
  @DisplayName("Scénario c : Si les parents sont séparés, le parent avec qui l'enfant vit a droit à l'allocation (Enfant vivant avec Parent 2)")
  void givenParentsSeparatedAndChildLivesWithParent2_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent2() {
    famille.setParentsViventEnsemble(false);
    famille.setEnfantResidence(famille.getParent2Residence());

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent2", result);
  }

  @Test
  @DisplayName("Scénario d : Si les parents vivent ensemble, le parent qui travaille dans le canton de domicile de l'enfant a droit à l'allocation (Parent 1 travaille dans le canton)")
  void givenParentsTogetherAndParent1WorksInChildCanton_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent1() {
    famille.setParent2TravailDansCantonDomicileEnfant(false);

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent1", result);
  }

  @Test
  @DisplayName("Scénario d : Si les parents vivent ensemble, le parent qui travaille dans le canton de domicile de l'enfant a droit à l'allocation (Parent 2 travaille dans le canton)")
  void givenParentsTogetherAndParent2WorksInChildCanton_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent2() {
    famille.setParent1TravailDansCantonDomicileEnfant(false);

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent2", result);
  }

  @Test
  @DisplayName("Scénario e : Si les deux parents sont salariés, le parent avec le revenu le plus élevé a droit à l'allocation (Parent 2 a un revenu plus élevé)")
  void givenBothParentsSalariedAndParent2HasHigherIncome_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent2() {
    famille.setParent2Salaire(famille.getParent1Salaire().multiply(BigDecimal.valueOf(2)));

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent2", result);
  }

  @Test
  @DisplayName("Scénario e : Si les deux parents sont salariés, le parent avec le revenu le plus élevé a droit à l'allocation (Parent 1 a un revenu plus élevé)")
  void givenBothParentsSalariedAndParent1HasHigherIncome_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent1() {
    famille.setParent1Salaire(famille.getParent2Salaire().multiply(BigDecimal.valueOf(2)));

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent1", result);
  }

  @Test
  @DisplayName("Scénario e : Si un parent est salarié et l'autre non, le parent salarié a droit à l'allocation (Parent 1 salarié)")
  void givenParentsTogetherAndParent1IsSalaried_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent1() {
    famille.setParent2EstSalarie(false);

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent1", result);
  }

  @Test
  @DisplayName("Scénario e : Si un parent est salarié et l'autre non, le parent salarié a droit à l'allocation (Parent 2 salarié)")
  void givenParentsTogetherAndParent2IsSalaried_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent2() {
    famille.setParent1EstSalarie(false);

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent2", result);
  }

  @Test
  @DisplayName("Scénario f : Si les deux parents sont indépendants, le parent avec le revenu le plus élevé a droit à l'allocation (Parent 1 a un revenu plus élevé)")
  void givenBothParentsIndependentAndParent1HasHigherIncome_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent1() {
    famille.setParent1EstSalarie(false);
    famille.setParent2EstSalarie(false);
    famille.setParent1Salaire(famille.getParent1Salaire().multiply(BigDecimal.valueOf(2)));

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent1", result);
  }

  @Test
  @DisplayName("Scénario f : Si les deux parents sont indépendants, le parent avec le revenu le plus élevé a droit à l'allocation (Parent 2 a un revenu plus élevé)")
  void givenBothParentsIndependentAndParent2HasHigherIncome_WhenDeterminingParentDroitAllocation_ThenShouldReturnParent2() {
    famille.setParent1EstSalarie(false);
    famille.setParent2EstSalarie(false);
    famille.setParent2Salaire(famille.getParent2Salaire().multiply(BigDecimal.valueOf(2)));

    String result = allocationService.getParentDroitAllocation(famille);
    assertEquals("Parent2", result);
  }

  @Test
  @DisplayName("Erreur : Si aucun des parents n'a une activité lucrative, une erreur est lancée")
  void givenNoParentHasWork_WhenDeterminingParentDroitAllocation_ThenShouldThrowException() {
    famille.setParent1ActiviteLucrative(false);
    famille.setParent2ActiviteLucrative(false);

    assertThrows(IllegalArgumentException.class,
            () -> allocationService.getParentDroitAllocation(famille));
  }
}
