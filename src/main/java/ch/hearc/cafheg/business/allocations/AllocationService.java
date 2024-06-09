package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.business.versements.VersementService;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Service pour gérer les allocations familiales.
 */
public class AllocationService {

  private static final String PARENT_1 = "Parent1";
  private static final String PARENT_2 = "Parent2";
  private static final Logger logger = LoggerFactory.getLogger(AllocationService.class);

  private final AllocataireMapper allocataireMapper;
  private final AllocationMapper allocationMapper;
  private final VersementService versementService;

  /**
   * Constructeur pour le service d'allocation.
   *
   * @param allocataireMapper Mapper pour les allocataires.
   * @param allocationMapper  Mapper pour les allocations.
   * @param versementService  Service pour les versements.
   */
  public AllocationService(AllocataireMapper allocataireMapper, AllocationMapper allocationMapper, VersementService versementService) {
    this.allocataireMapper = allocataireMapper;
    this.allocationMapper = allocationMapper;
    this.versementService = versementService;
  }

  /**
   * Récupère tous les allocataires avec un nom similaire.
   *
   * @param likeNom Filtre sur le nom des allocataires.
   * @return Liste des allocataires correspondant au filtre.
   */
  public List<Allocataire> findAllAllocataires(String likeNom) {
    logger.info("Récupération de tous les allocataires avec le filtre : {}", likeNom);
    return allocataireMapper.findAll(likeNom);
  }

  /**
   * Récupère toutes les allocations actuelles.
   *
   * @return Liste des allocations actuelles.
   */
  public List<Allocation> findAllocationsActuelles() {
    logger.info("Récupération des allocations actuelles.");
    return allocationMapper.findAll();
  }

  /**
   * Détermine le parent ayant droit à l'allocation.
   *
   * @param famille La famille pour laquelle déterminer le droit à l'allocation.
   * @return Le parent ayant droit à l'allocation.
   */
  public String getParentDroitAllocation(Famille famille) {
    logger.info("Détermination du parent ayant droit à l'allocation");

    if (onlyOneParentHasWork(famille)) {
      return getParentWithWork(famille);
    }

    if (bothParentsHaveWork(famille)) {
      return getParentBasedOnCriteria(famille);
    }

    throw new IllegalArgumentException("Paramètres invalides pour déterminer le parent ayant droit à l'allocation.");
  }

  /**
   * Vérifie si seulement un des parents a une activité lucrative.
   *
   * @param famille La famille à vérifier.
   * @return Vrai si un seul parent a une activité lucrative.
   */
  private boolean onlyOneParentHasWork(Famille famille) {
    return (famille.isParent1ActiviteLucrative() && !famille.isParent2ActiviteLucrative()) ||
            (!famille.isParent1ActiviteLucrative() && famille.isParent2ActiviteLucrative());
  }

  /**
   * Retourne le parent qui a une activité lucrative.
   *
   * @param famille La famille à vérifier.
   * @return Le parent qui a une activité lucrative.
   */
  private String getParentWithWork(Famille famille) {
    return famille.isParent1ActiviteLucrative() ? PARENT_1 : PARENT_2;
  }

  /**
   * Vérifie si les deux parents ont une activité lucrative.
   *
   * @param famille La famille à vérifier.
   * @return Vrai si les deux parents ont une activité lucrative.
   */
  private boolean bothParentsHaveWork(Famille famille) {
    return famille.isParent1ActiviteLucrative() && famille.isParent2ActiviteLucrative();
  }

  /**
   * Détermine le parent ayant droit à l'allocation selon certains critères.
   *
   * @param famille La famille à vérifier.
   * @return Le parent ayant droit à l'allocation.
   */
  private String getParentBasedOnCriteria(Famille famille) {
    if (famille.isParent1AutoriteParentale() && !famille.isParent2AutoriteParentale()) {
      return PARENT_1;
    }
    if (!famille.isParent1AutoriteParentale() && famille.isParent2AutoriteParentale()) {
      return PARENT_2;
    }

    if (!famille.isParentsViventEnsemble()) {
      return famille.getParent1Residence().equals(famille.getEnfantResidence()) ? PARENT_1 : PARENT_2;
    }

    if (famille.isParentsViventEnsemble()) {
      if (famille.isParent1TravailDansCantonDomicileEnfant() && !famille.isParent2TravailDansCantonDomicileEnfant()) {
        return PARENT_1;
      }
      if (famille.isParent2TravailDansCantonDomicileEnfant() && !famille.isParent1TravailDansCantonDomicileEnfant()) {
        return PARENT_2;
      }

      return compareSalaries(famille);
    }

    throw new IllegalArgumentException("Aucun critère ne correspond pour déterminer le parent ayant droit à l'allocation.");
  }

  /**
   * Compare les salaires des parents pour déterminer celui ayant droit à l'allocation.
   *
   * @param famille La famille à vérifier.
   * @return Le parent ayant droit à l'allocation basé sur les salaires.
   */
  private String compareSalaries(Famille famille) {
    if (famille.isParent1EstSalarie() && famille.isParent2EstSalarie()) {
      return famille.getParent1Salaire().compareTo(famille.getParent2Salaire()) > 0 ? PARENT_1 : PARENT_2;
    }

    if (famille.isParent1EstSalarie() && !famille.isParent2EstSalarie()) {
      return PARENT_1;
    }

    if (!famille.isParent1EstSalarie() && famille.isParent2EstSalarie()) {
      return PARENT_2;
    }

    return famille.getParent1Salaire().compareTo(famille.getParent2Salaire()) > 0 ? PARENT_1 : PARENT_2;
  }

  /**
   * Met à jour les informations d'un allocataire.
   *
   * @param id          Numéro AVS de l'allocataire.
   * @param newName     Nouveau nom de l'allocataire.
   * @param newFirstName Nouveau prénom de l'allocataire.
   */
  public void updateAllocataire(int id, String newName, String newFirstName) {
    logger.debug("Mise à jour de l'allocataire avec le numéro AVS : {}", id);
    Allocataire existingAllocataire = allocataireMapper.findById(id);
    if (existingAllocataire == null) {
      throw new IllegalArgumentException("Allocataire non trouvé");
    }

    boolean nameChanged = !Objects.equals(existingAllocataire.getNom(), newName);
    boolean firstNameChanged = !Objects.equals(existingAllocataire.getPrenom(), newFirstName);

    if (nameChanged || firstNameChanged) {
      Allocataire updatedAllocataire = new Allocataire(existingAllocataire.getNoAVS(), newName, newFirstName);
      allocataireMapper.update(updatedAllocataire);
      logger.info("Allocataire mis à jour avec succès : {}", id);
    } else {
      throw new IllegalArgumentException("Aucun changement détecté pour le nom ou le prénom");
    }
  }

  /**
   * Supprime un allocataire.
   *
   * @param id Numéro AVS de l'allocataire.
   */
  public void deleteAllocataire(int id) {
    logger.debug("Suppression de l'allocataire avec le numéro AVS : {}", id);

    // Récupérer l'allocataire par son ID
    Allocataire allocataire = allocataireMapper.findById(id);
    if (allocataire == null) {
      logger.error("Allocataire non trouvé pour le numéro AVS : {}", id);
      throw new IllegalArgumentException("Allocataire non trouvé");
    }

    // Vérifier si l'allocataire possède des versements
    if (versementService.hasVersementsForAllocataire(id)) {
      logger.error("Impossible de supprimer l'allocataire car des versements existent pour le numéro AVS : {}", id);
      throw new IllegalStateException("Impossible de supprimer l'allocataire car des versements existent");
    }

    // Supprimer l'allocataire
    allocataireMapper.delete(id);
    logger.info("Allocataire supprimé avec succès : {}", id);
  }
}
