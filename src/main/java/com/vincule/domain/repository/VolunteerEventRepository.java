package com.vincule.domain.repository;

import com.vincule.domain.entity.VolunteerEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VolunteerEventRepository extends JpaRepository<VolunteerEvent, UUID> {
    List<VolunteerEvent> findByOrganizationId(UUID organizationId);
    List<VolunteerEvent> findByEventDateAfter(LocalDateTime date);
}
