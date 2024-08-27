package com.example.calendrier.service;

import com.example.calendrier.entity.MembreEntreprise;
import com.example.calendrier.repository.MembreEntrepriseRepository;
import com.example.calendrier.service.interfaces.MembreEntrepriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MembreEntrepriseServiceImpl implements MembreEntrepriseService {
    private final MembreEntrepriseRepository membreEntrepriseRepository;

    @Override
    public MembreEntreprise createMembreEntreprise(MembreEntreprise membreEntreprise) {
        return membreEntrepriseRepository.save(membreEntreprise);
    }

    @Override
    public void save(MultipartFile file) {
        try {
            List<MembreEntreprise> MOCs = CSVHelper.csvToMOCs(file.getInputStream());
            membreEntrepriseRepository.saveAll(MOCs);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }

    }

    @Override
    public ByteArrayInputStream load() {
        List<MembreEntreprise> MOCs = membreEntrepriseRepository.findAll();

        ByteArrayInputStream in = CSVHelper.MOCsToCSV(MOCs);
        return in;
    }

    @Override
    public List<MembreEntreprise> findAllMembreEntreprise() {
        return membreEntrepriseRepository.findAll();
    }

    @Override
    public List<MembreEntreprise> findMembreEntrepriseByRole(String role) {
        return membreEntrepriseRepository.findByRole(role);
    }

    @Override
    public MembreEntreprise findMembreEntrepriseByMatricule(String matricule) {
        return membreEntrepriseRepository.findByMatricule(matricule);
    }

    @Override
    public MembreEntreprise updateMembreEntreprise(MembreEntreprise membre) {
        return membreEntrepriseRepository.save(membre);
    }

    @Override
    public void removeMembreEntreprise(String matricule) {
        MembreEntreprise membreEntreprise=findMembreEntrepriseByMatricule(matricule);
        membreEntrepriseRepository.delete(membreEntreprise);
    }
}