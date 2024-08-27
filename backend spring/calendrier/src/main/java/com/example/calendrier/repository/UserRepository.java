package com.example.calendrier.repository;

import com.example.calendrier.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUserByRole(User.ERole role);
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByMatricule(String matricule);
    Boolean existsByEmail(String email);
    User findByVerificationCode(String code);
    User findByEmail(String email);

    boolean existsByCin(String cin);
    User findByResetPasswordToken(String token);
    List<User> findUsersByDepartement(User.Departement departement);
}