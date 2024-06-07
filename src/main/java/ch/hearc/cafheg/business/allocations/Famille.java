package ch.hearc.cafheg.business.allocations;

import java.math.BigDecimal;

public class Famille {
    private boolean parent1ActiviteLucrative;
    private boolean parent2ActiviteLucrative;
    private boolean parent1AutoriteParentale;
    private boolean parent2AutoriteParentale;
    private boolean parentsViventEnsemble;
    private boolean parent1TravailDansCantonDomicileEnfant;
    private boolean parent2TravailDansCantonDomicileEnfant;
    private boolean parent1EstSalarie;
    private boolean parent2EstSalarie;
    private boolean parent1EstIndependant;
    private boolean parent2EstIndependant;
    private BigDecimal parent1Salaire;
    private BigDecimal parent2Salaire;
    private String parent1Residence;
    private String parent2Residence;
    private String enfantResidence;

    // Constructeur
    public Famille(boolean parent1ActiviteLucrative, boolean parent2ActiviteLucrative,
                   boolean parent1AutoriteParentale, boolean parent2AutoriteParentale,
                   boolean parentsViventEnsemble, boolean parent1TravailDansCantonDomicileEnfant,
                   boolean parent2TravailDansCantonDomicileEnfant, boolean parent1EstSalarie,
                   boolean parent2EstSalarie, boolean parent1EstIndependant, boolean parent2EstIndependant,
                   BigDecimal parent1Salaire, BigDecimal parent2Salaire, String parent1Residence,
                   String parent2Residence, String enfantResidence) {
        this.parent1ActiviteLucrative = parent1ActiviteLucrative;
        this.parent2ActiviteLucrative = parent2ActiviteLucrative;
        this.parent1AutoriteParentale = parent1AutoriteParentale;
        this.parent2AutoriteParentale = parent2AutoriteParentale;
        this.parentsViventEnsemble = parentsViventEnsemble;
        this.parent1TravailDansCantonDomicileEnfant = parent1TravailDansCantonDomicileEnfant;
        this.parent2TravailDansCantonDomicileEnfant = parent2TravailDansCantonDomicileEnfant;
        this.parent1EstSalarie = parent1EstSalarie;
        this.parent2EstSalarie = parent2EstSalarie;
        this.parent1EstIndependant = parent1EstIndependant;
        this.parent2EstIndependant = parent2EstIndependant;
        this.parent1Salaire = parent1Salaire;
        this.parent2Salaire = parent2Salaire;
        this.parent1Residence = parent1Residence;
        this.parent2Residence = parent2Residence;
        this.enfantResidence = enfantResidence;
    }

    // Getters et setters
    public boolean isParent1ActiviteLucrative() {
        return parent1ActiviteLucrative;
    }

    public void setParent1ActiviteLucrative(boolean parent1ActiviteLucrative) {
        this.parent1ActiviteLucrative = parent1ActiviteLucrative;
    }

    public boolean isParent2ActiviteLucrative() {
        return parent2ActiviteLucrative;
    }

    public void setParent2ActiviteLucrative(boolean parent2ActiviteLucrative) {
        this.parent2ActiviteLucrative = parent2ActiviteLucrative;
    }

    public boolean isParent1AutoriteParentale() {
        return parent1AutoriteParentale;
    }

    public void setParent1AutoriteParentale(boolean parent1AutoriteParentale) {
        this.parent1AutoriteParentale = parent1AutoriteParentale;
    }

    public boolean isParent2AutoriteParentale() {
        return parent2AutoriteParentale;
    }

    public void setParent2AutoriteParentale(boolean parent2AutoriteParentale) {
        this.parent2AutoriteParentale = parent2AutoriteParentale;
    }

    public boolean isParentsViventEnsemble() {
        return parentsViventEnsemble;
    }

    public void setParentsViventEnsemble(boolean parentsViventEnsemble) {
        this.parentsViventEnsemble = parentsViventEnsemble;
    }

    public boolean isParent1TravailDansCantonDomicileEnfant() {
        return parent1TravailDansCantonDomicileEnfant;
    }

    public void setParent1TravailDansCantonDomicileEnfant(boolean parent1TravailDansCantonDomicileEnfant) {
        this.parent1TravailDansCantonDomicileEnfant = parent1TravailDansCantonDomicileEnfant;
    }

    public boolean isParent2TravailDansCantonDomicileEnfant() {
        return parent2TravailDansCantonDomicileEnfant;
    }

    public void setParent2TravailDansCantonDomicileEnfant(boolean parent2TravailDansCantonDomicileEnfant) {
        this.parent2TravailDansCantonDomicileEnfant = parent2TravailDansCantonDomicileEnfant;
    }

    public boolean isParent1EstSalarie() {
        return parent1EstSalarie;
    }

    public void setParent1EstSalarie(boolean parent1EstSalarie) {
        this.parent1EstSalarie = parent1EstSalarie;
    }

    public boolean isParent2EstSalarie() {
        return parent2EstSalarie;
    }

    public void setParent2EstSalarie(boolean parent2EstSalarie) {
        this.parent2EstSalarie = parent2EstSalarie;
    }

    public boolean isParent1EstIndependant() {
        return parent1EstIndependant;
    }

    public void setParent1EstIndependant(boolean parent1EstIndependant) {
        this.parent1EstIndependant = parent1EstIndependant;
    }

    public boolean isParent2EstIndependant() {
        return parent2EstIndependant;
    }

    public void setParent2EstIndependant(boolean parent2EstIndependant) {
        this.parent2EstIndependant = parent2EstIndependant;
    }

    public BigDecimal getParent1Salaire() {
        return parent1Salaire;
    }

    public void setParent1Salaire(BigDecimal parent1Salaire) {
        this.parent1Salaire = parent1Salaire;
    }

    public BigDecimal getParent2Salaire() {
        return parent2Salaire;
    }

    public void setParent2Salaire(BigDecimal parent2Salaire) {
        this.parent2Salaire = parent2Salaire;
    }

    public String getParent1Residence() {
        return parent1Residence;
    }

    public void setParent1Residence(String parent1Residence) {
        this.parent1Residence = parent1Residence;
    }

    public String getParent2Residence() {
        return parent2Residence;
    }

    public void setParent2Residence(String parent2Residence) {
        this.parent2Residence = parent2Residence;
    }

    public String getEnfantResidence() {
        return enfantResidence;
    }

    public void setEnfantResidence(String enfantResidence) {
        this.enfantResidence = enfantResidence;
    }
}
