package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findByEmailIgnoreCase(String email);
}
