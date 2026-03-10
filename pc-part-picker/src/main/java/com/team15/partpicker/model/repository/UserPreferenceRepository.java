package com.team15.partpicker.model.repository;

import com.team15.partpicker.model.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
}
