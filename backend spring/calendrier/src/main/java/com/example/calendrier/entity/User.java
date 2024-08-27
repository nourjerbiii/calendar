package com.example.calendrier.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User implements UserDetails {
    public enum Poste{RH,MANAGER,EMPLOYEE}
    public enum Departement{RH,CONSEIL,SOLUTIONS,EXPERTISE}
    public enum EtatCivil{CELIBATAIRE,MARIE,DIVORCE}
    public enum ERole {ROLE_ADMIN, ROLE_EMPLOYEE,ROLE_MANAGER}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Size(max = 20)
    private String username;

    @Column(unique = true)
    @Size(max = 50)
    @Email
    private String email;

    @Size(max = 120)
    private String password;
    @Enumerated(EnumType.STRING)
    private ERole role;
    @Column(unique = true)
    private String  cin;
    private String  resetPasswordToken;
    private String  verificationCode ;
    private boolean blocked;
    @Column(unique = true)
    private String  matricule;
    private String  nom;
    private String  prenom;
    private Date dateNaissance;
    private Integer numTelephone;
    private String  adresse;
    private Date    dateEmbauche;
    private String  urgenceNom;
    private Integer urgenceNum;
    private boolean approved;

    @Temporal(TemporalType.TIMESTAMP)
    private Date        dateCreation;
    @Enumerated(EnumType.STRING)
    private Poste       poste;
    @Enumerated(EnumType.STRING)
    private Departement departement;
    @Enumerated(EnumType.STRING)
    private EtatCivil etatCivil;

    @PrePersist
    private void prePersist() {
        dateCreation = new Date();
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    private Boolean isBlocked;
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public boolean isAccountNonLocked() {
        return Boolean.FALSE.equals(isBlocked);
    }

    @OneToMany(mappedBy = "assignedto",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Task> assignedTasks;

    @OneToMany(mappedBy = "craetedby",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Task> createdTasks;

    @PreRemove
    private void preRemove() {
        for (Task task : assignedTasks) {
            task.setAssignedto(null);
        }
        for (Task task : createdTasks) {
            task.setCraetedby(null);
        }
    }

}
