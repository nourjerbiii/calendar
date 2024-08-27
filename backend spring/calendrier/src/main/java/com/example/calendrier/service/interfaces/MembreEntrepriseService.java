package com.example.calendrier.service.interfaces;

import com.example.calendrier.entity.MembreEntreprise;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface MembreEntrepriseService {
    MembreEntreprise createMembreEntreprise(MembreEntreprise membreEntreprise);
    public void save(MultipartFile file);
    ByteArrayInputStream load();
    List<MembreEntreprise> findAllMembreEntreprise();
    List<MembreEntreprise> findMembreEntrepriseByRole(String role);
    MembreEntreprise findMembreEntrepriseByMatricule(String matricule);
    MembreEntreprise updateMembreEntreprise (MembreEntreprise membre);
    void removeMembreEntreprise(String matricule);
}
