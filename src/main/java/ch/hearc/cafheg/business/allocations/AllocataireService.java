package ch.hearc.cafheg.business.allocations;

import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;

import java.util.Optional;

public class AllocataireService {

    private final AllocataireMapper allocataireMapper;
    private final AllocationMapper allocationMapper;

    public AllocataireService(AllocataireMapper allocataireMapper, AllocationMapper allocationMapper) {
        this.allocataireMapper = allocataireMapper;
        this.allocationMapper = allocationMapper;
    }

    public Allocataire updateAllocataire(NoAVS noAVS, String nouveauNom, String nouveauPrenom) {
        // Convert the NoAVS string value to long
        long noAVSValue;
        try {
            noAVSValue = Long.parseLong(noAVS.getValue());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("NoAVS value is not a valid long", e);
        }

        Optional<Allocataire> existingAllocataire = Optional.ofNullable(allocataireMapper.findById(noAVSValue));
        if (existingAllocataire.isPresent()) {
            Allocataire updatedAllocataire = new Allocataire(noAVS, nouveauNom, nouveauPrenom);
            allocataireMapper.update(updatedAllocataire);
            return updatedAllocataire;
        } else {
            throw new RuntimeException("Allocataire non trouvé avec NoAVS : " + noAVS);
        }
    }

    public Allocataire insertAllocataire(String noAVSValue, String nom, String prenom) {
        // Convert the NoAVS string value to long
        long noAVS;
        try {
            noAVS = Long.parseLong(noAVSValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("NoAVS value is not a valid long", e);
        }

        NoAVS noAVSObject = new NoAVS(noAVSValue); // Assumes a constructor accepting a String
        Allocataire newAllocataire = new Allocataire(noAVSObject, nom, prenom);
        allocataireMapper.insert(newAllocataire);
        return newAllocataire;
    }

    public boolean deleteAllocataire(String noAVSValue) {
        // Convert the NoAVS string value to long
        long noAVS;
        try {
            noAVS = Long.parseLong(noAVSValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("NoAVS value is not a valid long", e);
        }

        // Check if Allocataire exists before deleting
        Optional<Allocataire> existingAllocataire = Optional.ofNullable(allocataireMapper.findById(noAVS));
        if (existingAllocataire.isPresent()) {
            allocataireMapper.delete(new NoAVS(noAVSValue)); // Assumes a constructor accepting a String
            return true;
        } else {
            throw new RuntimeException("Tentative de suppression d'un allocataire non existant avec NoAVS : " + noAVSValue);
        }
    }
}
