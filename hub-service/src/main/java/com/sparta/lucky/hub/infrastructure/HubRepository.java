package com.sparta.lucky.hub.infrastructure;

import com.sparta.lucky.hub.domain.Hub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID> {

    Optional<Hub> findByIdAndDeletedAtIsNull(UUID id);

    Optional<Hub> findByManagerIdAndDeletedAtIsNull(UUID managerId);

    Page<Hub> findAllByDeletedAtIsNull(Pageable pageable);

    List<Hub> findAllByDeletedAtIsNull();
}