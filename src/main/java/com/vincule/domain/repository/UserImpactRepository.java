package com.vincule.domain.repository;

import com.vincule.domain.entity.UserImpact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserImpactRepository extends JpaRepository<UserImpact, UUID> {
}
