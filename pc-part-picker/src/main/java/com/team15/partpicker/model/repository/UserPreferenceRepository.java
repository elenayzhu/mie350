package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    List<UserPreference> findByUserProfile_IdOrderByIdDesc(Long userProfileId);
}
