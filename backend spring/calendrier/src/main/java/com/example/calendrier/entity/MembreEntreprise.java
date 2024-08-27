package com.example.calendrier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembreEntreprise {
    @Id
    @Column(unique = true)
    private String matricule;
    private String name;
    private String lastName;
    private String role;
}
