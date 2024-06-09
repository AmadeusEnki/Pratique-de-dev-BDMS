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

  private static final Logger Logger = LoggerFactory.getLogger(RESTController.class);

  private final AllocationService allocationService;
  private final VersementService versementService;

  // Constructeur pour initialiser les services nécessaires
  public RESTController(AllocataireMapper allocataireMapper, AllocationMapper allocationMapper, VersementMapper versementMapper, EnfantMapper enfantMapper, PDFExporter pdfExporter) {
    this.allocationService = new AllocationService(allocataireMapper, allocationMapper, new VersementService(versementMapper, allocataireMapper, pdfExporter));
    this.versementService = new VersementService(versementMapper, allocataireMapper, pdfExporter);
  }

  /*
   * Endpoint pour déterminer quel parent a droit à l'allocation.
   * Requiert un JSON avec les informations sur la famille.
   */
  @PostMapping("/droits/quel-parent")
  public String getParentDroitAllocation(@RequestBody Famille params) {
    Logger.info("Requête reçue pour déterminer le parent ayant droit à l'allocation avec les paramètres : {}", params);
    return inTransaction(() -> allocationService.getParentDroitAllocation(params));
  }

  /*
   * Endpoint pour récupérer tous les allocataires dont le nom commence par une chaîne donnée.
   * Si le paramètre 'startsWith' n'est pas fourni, retourne tous les allocataires.
   */
  @GetMapping("/allocataires")
  public List<Allocataire> allocataires(@RequestParam(value = "startsWith", required = false) String start) {
    Logger.info("Requête reçue pour récupérer tous les allocataires commençant par : {}", start);
    return inTransaction(() -> allocationService.findAllAllocataires(start));
  }

  /*
   * Endpoint pour récupérer toutes les allocations actuelles.
   */
  @GetMapping("/allocations")
  public List<Allocation> allocations() {
    Logger.info("Requête reçue pour récupérer toutes les allocations actuelles.");
    return inTransaction(allocationService::findAllocationsActuelles);
  }

  /*
   * Endpoint pour récupérer la somme des allocations pour une année donnée.
   */
  @GetMapping("/allocations/{year}/somme")
  public BigDecimal sommeAs(@PathVariable("year") int year) {
    Logger.info("Requête reçue pour récupérer la somme des allocations pour l'année : {}", year);
    return inTransaction(() -> versementService.findSommeAllocationParAnnee(year).getValue());
  }

  /*
   * Endpoint pour récupérer la somme des allocations de naissance pour une année donnée.
   */
  @GetMapping("/allocations-naissances/{year}/somme")
  public BigDecimal sommeAns(@PathVariable("year") int year) {
    Logger.info("Requête reçue pour récupérer la somme des allocations de naissance pour l'année : {}", year);
    return inTransaction(() -> versementService.findSommeAllocationNaissanceParAnnee(year).getValue());
  }

  /*
   * Endpoint pour générer un PDF des allocations pour un allocataire donné.
   * Le PDF est retourné en tant que tableau de bytes.
   */
  @GetMapping(value = "/allocataires/{allocataireId}/allocations", produces = MediaType.APPLICATION_PDF_VALUE)
  public byte[] pdfAllocations(@PathVariable("allocataireId") int allocataireId) {
    Logger.info("Requête reçue pour générer le PDF des allocations pour l'allocataire ID : {}", allocataireId);
    return inTransaction(() -> versementService.exportPDFAllocataire(allocataireId));
  }

  /*
   * Endpoint pour générer un PDF des versements pour un allocataire donné.
   * Le PDF est retourné en tant que tableau de bytes.
   */
  @GetMapping(value = "/allocataires/{allocataireId}/versements", produces = MediaType.APPLICATION_PDF_VALUE)
  public byte[] pdfVersements(@PathVariable("allocataireId") int allocataireId) {
    Logger.info("Requête reçue pour générer le PDF des versements pour l'allocataire ID : {}", allocataireId);
    return inTransaction(() -> versementService.exportPDFVersements(allocataireId));
  }

  /*
   * Endpoint pour mettre à jour le nom et le prénom d'un allocataire.
   * Requiert un JSON avec les nouvelles valeurs pour 'nom' et 'prenom'.
   */
  @PutMapping("/allocataire/{id}")
  public void updateAllocataire(@PathVariable("id") int id, @RequestBody Map<String, String> params) {
    String newNom = params.get("nom");
    String newPrenom = params.get("prenom");
    Logger.info("Requête reçue pour mettre à jour l'allocataire avec l'ID : {}. Nouveau nom : {}, nouveau prénom : {}", id, newNom, newPrenom);
    inTransaction(() -> {
      allocationService.updateAllocataire(id, newNom, newPrenom);
      Logger.info("Allocataire mis à jour avec l'ID : {}", id);
      return null;
    });
  }

  /*
   * Endpoint pour supprimer un allocataire.
   */
  @DeleteMapping("/allocataire/{id}")
  public void deleteAllocataire(@PathVariable("id") int id) {
    Logger.info("Requête reçue pour supprimer l'allocataire avec l'ID : {}", id);
    inTransaction(() -> {
      allocationService.deleteAllocataire(id);
      Logger.info("Allocataire supprimé avec l'ID : {}", id);
      return null;
    });
  }
}
