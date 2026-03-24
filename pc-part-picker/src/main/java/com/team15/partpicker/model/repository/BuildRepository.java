package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.Build;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildRepository extends JpaRepository<Build, Long> {

    List<Build> findByUserPreferenceIdOrderByCreatedAtDesc(Long preferenceId);

    List<Build> findByUserProfile_IdOrderByCreatedAtDesc(Long userProfileId);
}
