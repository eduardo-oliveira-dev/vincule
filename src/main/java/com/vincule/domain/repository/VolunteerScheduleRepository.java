package com.vincule.domain.repository;

import com.vincule.domain.entity.VolunteerSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VolunteerScheduleRepository extends JpaRepository<VolunteerSchedule, UUID> {
    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);
    long countByEventId(UUID eventId);
}
