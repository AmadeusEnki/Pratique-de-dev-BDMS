package ch.hearc.cafheg.infrastructure.api;

import static ch.hearc.cafheg.infrastructure.persistance.Database.inTransaction;

import ch.hearc.cafheg.business.allocations.Allocataire;
import ch.hearc.cafheg.business.allocations.Allocation;
import ch.hearc.cafheg.business.allocations.AllocationService;
import ch.hearc.cafheg.business.allocations.Famille;
import ch.hearc.cafheg.business.versements.VersementService;
import ch.hearc.cafheg.infrastructure.pdf.PDFExporter;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import ch.hearc.cafheg.infrastructure.persistance.EnfantMapper;
import ch.hearc.cafheg.infrastructure.persistance.VersementMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
public class RESTController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RESTController.class);

  private final AllocationService allocationService;
  private final VersementService versementService;

  public RESTController() {
    this.allocationService = new AllocationService(new AllocataireMapper(), new AllocationMapper());
    this.versementService = new VersementService(new VersementMapper(), new AllocataireMapper(),
            new PDFExporter(new EnfantMapper()));
  }

  /*
  // Headers de la requête HTTP doit contenir "Content-Type: application/json"
  // BODY de la requête HTTP à transmettre afin de tester le endpoint
  {
      "enfantResidence" : "Neuchâtel",
      "parent1Residence" : "Neuchâtel",
      "parent2Residence" : "Bienne",
      "parent1ActiviteLucrative" : true,
      "parent2ActiviteLucrative" : true,
      "parent1Salaire" : 2500,
      "parent2Salaire" : 3000
  }
   */
  @PostMapping("/droits/quel-parent")
  public String getParentDroitAllocation(@RequestBody Famille params) {
    LOGGER.info("Requête reçue pour déterminer le parent ayant droit à l'allocation avec les paramètres : {}", params);
    return inTransaction(() -> allocationService.getParentDroitAllocation(params));
  }

  @GetMapping("/allocataires")
  public List<Allocataire> allocataires(
          @RequestParam(value = "startsWith", required = false) String start) {
    LOGGER.info("Requête reçue pour récupérer tous les allocataires commençant par : {}", start);
    return inTransaction(() -> allocationService.findAllAllocataires(start));
  }

  @GetMapping("/allocations")
  public List<Allocation> allocations() {
    LOGGER.info("Requête reçue pour récupérer toutes les allocations actuelles.");
    return inTransaction(allocationService::findAllocationsActuelles);
  }

  @GetMapping("/allocations/{year}/somme")
  public BigDecimal sommeAs(@PathVariable("year") int year) {
    LOGGER.info("Requête reçue pour récupérer la somme des allocations pour l'année : {}", year);
    return inTransaction(() -> versementService.findSommeAllocationParAnnee(year).getValue());
  }

  @GetMapping("/allocations-naissances/{year}/somme")
  public BigDecimal sommeAns(@PathVariable("year") int year) {
    LOGGER.info("Requête reçue pour récupérer la somme des allocations de naissance pour l'année : {}", year);
    return inTransaction(
            () -> versementService.findSommeAllocationNaissanceParAnnee(year).getValue());
  }

  @GetMapping(value = "/allocataires/{allocataireId}/allocations", produces = MediaType.APPLICATION_PDF_VALUE)
  public byte[] pdfAllocations(@PathVariable("allocataireId") int allocataireId) {
    LOGGER.info("Requête reçue pour générer le PDF des allocations pour l'allocataire ID : {}", allocataireId);
    return inTransaction(() -> versementService.exportPDFAllocataire(allocataireId));
  }

  @GetMapping(value = "/allocataires/{allocataireId}/versements", produces = MediaType.APPLICATION_PDF_VALUE)
  public byte[] pdfVersements(@PathVariable("allocataireId") int allocataireId) {
    LOGGER.info("Requête reçue pour générer le PDF des versements pour l'allocataire ID : {}", allocataireId);
    return inTransaction(() -> versementService.exportPDFVersements(allocataireId));
  }

  @PutMapping("/{id}")
  public void updateAllocataire(@PathVariable("id") int id, @RequestBody Map<String, String> params) {
    String newNom = params.get("nom");
    String newPrenom = params.get("prenom");
    LOGGER.info("Received request to update allocataire with id: {}. New nom: {}, new prenom: {}", id, newNom, newPrenom);
    inTransaction(() -> {
      allocationService.updateAllocataire(id, newNom, newPrenom);
      LOGGER.info("Updated allocataire with id: {}", id);
      return null;
    });
  }

  @DeleteMapping("/{id}")
  public void deleteAllocataire(@PathVariable("id") int id){
    LOGGER.info("Received request to delete allocataire with id: {}", id);
    inTransaction(() -> {
      allocationService.deleteAllocataire(id);
      LOGGER.info("Deleted allocataire with id: {}", id);
      return null;
    });
  }
}
