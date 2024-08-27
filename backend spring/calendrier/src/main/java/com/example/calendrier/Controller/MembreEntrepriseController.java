package com.example.calendrier.Controller;

import com.example.calendrier.entity.MembreEntreprise;

import com.example.calendrier.payload.response.MessageResponse;
import com.example.calendrier.repository.MembreEntrepriseRepository;
import com.example.calendrier.service.CSVHelper;
import com.example.calendrier.service.interfaces.MembreEntrepriseService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/membre")
@RequiredArgsConstructor

public class MembreEntrepriseController {
    private final MembreEntrepriseService membreEntrepriseService;
    private final MembreEntrepriseRepository membreEntrepriseRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createMembre(@RequestBody MembreEntreprise membreEntreprise) {
        if (membreEntrepriseRepository.findByMatricule(membreEntreprise.getMatricule()) != null) {
            String errorMessage = "Le membre avec le Matricule " + membreEntreprise.getMatricule() + " existe déjà.";
            return ResponseEntity.badRequest().body(new MessageResponse(errorMessage));
        }
        MembreEntreprise createdMembre = membreEntrepriseRepository.save(membreEntreprise);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMembre);
    }

    @GetMapping("/findallmembre")
    List <MembreEntreprise> findAllMembreList() {
return membreEntrepriseService.findAllMembreEntreprise();
    }

    @GetMapping("/getmembrebyrole/{role}")
    public List<MembreEntreprise> findMembrebyRole(@PathVariable String role){
        return membreEntrepriseService.findMembreEntrepriseByRole(role);
    }

    @GetMapping("/getmembrebymatricule/{matricule}")
    public MembreEntreprise findmmMembrebyMatricule(@PathVariable String matricule){
        return membreEntrepriseService.findMembreEntrepriseByMatricule(matricule);
    }

    @PutMapping("/updatemembre")
    public MembreEntreprise updateMembre(@RequestBody MembreEntreprise membre){
        return membreEntrepriseService.updateMembreEntreprise(membre);
    }

    @DeleteMapping("/deletemembre/{matricule}")
    public  void deleteMembre(@PathVariable String matricule){
        membreEntrepriseService.removeMembreEntreprise(matricule);
    }
    @GetMapping("/download")
    public ResponseEntity<Resource> getFile() {
        String filename = "membres of company.csv";
        InputStreamResource file = new InputStreamResource(membreEntrepriseService.load());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }

    @PostMapping("/upload")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String message;

        if (CSVHelper.hasCSVFormat(file)) {
            try {
                membreEntrepriseService.save(file);

                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new MessageResponse(message));
            }
        }

        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(message));
    }

}

