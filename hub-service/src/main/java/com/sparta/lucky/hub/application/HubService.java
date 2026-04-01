package com.sparta.lucky.hub.application;

import com.sparta.lucky.hub.application.dto.*;
import com.sparta.lucky.hub.common.exception.BusinessException;
import com.sparta.lucky.hub.common.exception.HubErrorCode;
import com.sparta.lucky.hub.domain.Hub;
import com.sparta.lucky.hub.infrastructure.HubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubService {

    private final HubRepository hubRepository;

    @Transactional
    public CreateHubResult createHub(CreateHubCommand command) {
        Hub hub = Hub.create(command.getName(), command.getAddress(), command.getLatitude(), command.getLongitude());
        return CreateHubResult.from(hubRepository.save(hub));
    }

    @Transactional(readOnly = true)
    public GetHubResult getHub(UUID hubId) {
        Hub hub = findActiveHub(hubId);
        return GetHubResult.from(hub);
    }

    @Transactional(readOnly = true)
    public Page<GetHubResult> getHubs(Pageable pageable) {
        return hubRepository.findAllByDeletedAtIsNull(pageable)
                .map(GetHubResult::from);
    }

    @Transactional
    public GetHubResult updateHub(UpdateHubCommand command) {
        Hub hub = findActiveHub(command.getHubId());
        hub.update(command.getName(), command.getAddress(), command.getLatitude(), command.getLongitude());
        return GetHubResult.from(hub);
    }

    @Transactional
    public void assignManager(AssignManagerCommand command) {
        Hub hub = findActiveHub(command.getHubId());
        hub.assignManager(command.getManagerId());
    }

    @Transactional
    public void deleteHub(UUID hubId, UUID deletedBy) {
        Hub hub = findActiveHub(hubId);
        hub.softDelete(deletedBy);
    }

    private Hub findActiveHub(UUID hubId) {
        return hubRepository.findByIdAndDeletedAtIsNull(hubId)
                .orElseThrow(() -> new BusinessException(HubErrorCode.HUB_NOT_FOUND));
    }

    public List<Hub> getHubsList() {
        return hubRepository.findAllByDeletedAtIsNull();
    }
}