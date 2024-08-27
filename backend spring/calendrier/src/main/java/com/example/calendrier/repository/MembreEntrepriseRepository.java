package com.example.calendrier.repository;

import com.example.calendrier.entity.MembreEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembreEntrepriseRepository extends JpaRepository<MembreEntreprise, String> {
    boolean existsByMatricule(String matricule);
    MembreEntreprise findByMatricule(String matricule);
    List<MembreEntreprise> findByRole(String Role);
    void deleteMembreEntrepriseByMatricule(String matricule);
    void delete(MembreEntreprise membreEntreprise);
}