package com.sparta.lucky.company.application;

import com.sparta.lucky.company.application.dto.*;
import com.sparta.lucky.company.common.exception.BusinessException;
import com.sparta.lucky.company.domain.Company;
import com.sparta.lucky.company.domain.CompanyErrorCode;
import com.sparta.lucky.company.domain.CompanyRepository;
import com.sparta.lucky.company.domain.CompanyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 역할 검증은 게이트웨이 구현 완료 후 @PreAuthorize로 교체? - 협의필요
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 기본 읽기 전용, 쓰기는 각 메서드에 @Transactional 추가
public class CompanyService {

    // 매직스트링 상수화
    private static final String ROLE_MASTER = "MASTER";
    private static final String ROLE_HUB_MANAGER = "HUB_MANAGER";
    private static final String ROLE_COMPANY_MANAGER = "COMPANY_MANAGER";

    private final CompanyRepository companyRepository;
    // TODO: HubClient hubClient — hub 존재 여부 검증 (hub-service 구현 후 추가)
    // TODO: ProductInternalClient productClient — 업체 삭제 시 상품 일괄 삭제

    @Transactional
    public CreateCompanyResult createCompany(CreateCompanyCommand command) {
        log.info("업체 생성 요청 - requester: {}, role: {}, hubId: {}",
                command.getRequesterId(), command.getRequesterRole(), command.getHubId());

        // MASTER, HUB_MANAGER만 생성 가능
        if (!ROLE_MASTER.equals(command.getRequesterRole())
                && !ROLE_HUB_MANAGER.equals(command.getRequesterRole())) {
            log.warn("업체 생성 권한 없음 - requester: {}, role: {}",
                    command.getRequesterId(), command.getRequesterRole());
            throw new BusinessException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
        }

        // HUB_MANAGER는 자신의 허브에만 업체 생성 가능
        if (ROLE_HUB_MANAGER.equals(command.getRequesterRole())) {
            if (command.getHubId() == null || !command.getHubId().equals(command.getRequesterHubId())) {
                log.warn("업체 생성 권한 없음 - requester: {}, 요청 hubId: {}, 소속 hubId: {}",
                        command.getRequesterId(), command.getHubId(), command.getRequesterHubId());
                throw new BusinessException(CompanyErrorCode.COMPANY_HUB_MISMATCH);
            }
        }

        // TODO: hubClient.getHub(command.getHubId()) — 허브 실존 여부 검증

        Company company = Company.builder()
                .name(command.getName())
                .companyType(command.getCompanyType())
                .hubId(command.getHubId())
                .address(command.getAddress())
                // manager는 생성 시 null — MASTER가 내부 API로 나중에 배정
                .build();

        CreateCompanyResult result = CreateCompanyResult.from(companyRepository.save(company));
        log.info("업체 생성 완료 - companyId: {}, name: {}", result.getId(), result.getName());
        return result;
    }

    public Page<GetCompanyResult> getCompanies(String name, CompanyType type, UUID hubId, Pageable pageable) {
        // name 파라미터 유무에 따라 검색/전체 조회 분기 → Repository에서 처리
        log.debug("업체 목록 조회 - name: {}, type: {}, hubId: {}, page: {}",
                name, type, hubId, pageable.getPageNumber());
        return companyRepository.searchByName(name, type, hubId, pageable)
                .map(GetCompanyResult::from);
    }

    public GetCompanyResult getCompany(UUID companyId) {
        log.debug("업체 단건 조회 - companyId: {}", companyId);
        return companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .map(GetCompanyResult::from)
                .orElseThrow(() -> {
                    log.warn("업체 조회 실패 - companyId: {}", companyId);
                    return new BusinessException(CompanyErrorCode.COMPANY_NOT_FOUND);
                });
    }

    @Transactional
    public GetCompanyResult updateCompany(UpdateCompanyCommand command) {
        log.info("업체 수정 요청 - companyId: {}, requester: {}, role: {}",
                command.getCompanyId(), command.getRequesterId(), command.getRequesterRole());

        Company company = companyRepository.findByIdAndDeletedAtIsNull(command.getCompanyId())
                .orElseThrow(() -> {
                    log.warn("업체 수정 실패 - 업체 없음, companyId: {}", command.getCompanyId());
                    return new BusinessException(CompanyErrorCode.COMPANY_NOT_FOUND);
                });

        // 역할별 소유권 검증
        validateUpdateAccess(company, command);

        // hub_id 변경은 MASTER 전용
        if (command.getHubId() != null) {
            if (!ROLE_MASTER.equals(command.getRequesterRole())) {
                log.warn("허브 변경 권한 없음 - requester: {}, role: {}",
                        command.getRequesterId(), command.getRequesterRole());
                throw new BusinessException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
            }
            // TODO: 변경할 hub 존재 여부 검증
            company.changeHub(command.getHubId());
        }

        company.update(command.getName(), command.getCompanyType(), command.getAddress());
        log.info("업체 수정 완료 - companyId: {}", command.getCompanyId());
        return GetCompanyResult.from(company);
    }

    @Transactional
    public DeleteCompanyResult deleteCompany(UUID companyId, UUID requesterId,
                                             String requesterRole, UUID requesterHubId) {
        log.info("업체 삭제 요청 - companyId: {}, requester: {}, role: {}",
                companyId, requesterId, requesterRole);
        Company company = companyRepository.findByIdAndDeletedAtIsNull(companyId)
                .orElseThrow(() -> {
                    log.warn("업체 삭제 실패 - 업체 없음, companyId: {}", companyId);
                    return new BusinessException(CompanyErrorCode.COMPANY_NOT_FOUND);
                });

        // MASTER, HUB_MANAGER만 삭제 가능 — 그 외 역할은 즉시 차단
        if (!ROLE_MASTER.equals(requesterRole) && !ROLE_HUB_MANAGER.equals(requesterRole)) {
            log.warn("업체 삭제 권한 없음 - requester: {}, role: {}", requesterId, requesterRole);
            throw new BusinessException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
        }

        // HUB_MANAGER는 자기 허브 업체만 삭제 가능
        if (ROLE_HUB_MANAGER.equals(requesterRole)) {
            if (!company.getHubId().equals(requesterHubId)) {
                log.warn("업체 삭제 권한 없음 - 허브 불일치, requester: {}, 업체 hubId: {}, 소속 hubId: {}",
                        requesterId, company.getHubId(), requesterHubId);
                throw new BusinessException(CompanyErrorCode.COMPANY_HUB_MISMATCH);
            }
        }

        company.softDelete(requesterId);
        log.info("업체 삭제 완료 - companyId: {}, deletedBy: {}", companyId, requesterId);

        // TODO: productClient.deleteByCompanyId(companyId) — 하위 product 일괄 soft delete
        // 실패 시 부분 삭제 상태 발생 가능

        return DeleteCompanyResult.of(company.getDeletedAt(), company.getDeletedBy());
    }

    // user-service 내부 API — 담당자 배정
    @Transactional
    public void assignManager(AssignManagerCommand command) {
        log.info("업체담당자 배정 user-service로부터 요청 - companyId: {}, managerId: {}",
                command.getCompanyId(), command.getManagerId());

        Company company = companyRepository.findByIdAndDeletedAtIsNull(command.getCompanyId())
                .orElseThrow(() -> {
                    log.warn("담당자 배정 실패 - 업체 없음, companyId: {}", command.getCompanyId());
                    return new BusinessException(CompanyErrorCode.COMPANY_NOT_FOUND);
                });
        company.assignManager(command.getManagerId());
    }

    /**
     * 수정 권한 검증 내부메서드
     * MASTER — 전체 허용
     * HUB_MANAGER — 자기 허브 소속 업체만
     * COMPANY_MANAGER — 자기 소속 업체만
     */
    private void validateUpdateAccess(Company company, UpdateCompanyCommand command) {
        // null 체크 우선 진행
        if (command.getRequesterRole() == null) {
            log.warn("업체 수정 권한 없음 - 역할 정보 없음, requester: {}", command.getRequesterId());
            throw new BusinessException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
        }

        switch (command.getRequesterRole()) {
            case ROLE_MASTER -> { /* 전체 허용 */ }
            case ROLE_HUB_MANAGER -> {
                if (!company.getHubId().equals(command.getRequesterHubId())) {
                    log.warn("업체 수정 권한 없음 - 허브 불일치, requester: {}, 업체 hubId: {}, 소속 hubId: {}",
                            command.getRequesterId(), company.getHubId(), command.getRequesterHubId());
                    throw new BusinessException(CompanyErrorCode.COMPANY_HUB_MISMATCH);
                }
            }
            case ROLE_COMPANY_MANAGER -> {
                if (!company.getId().equals(command.getRequesterCompanyId())) {
                    log.warn("업체 수정 권한 없음 - 업체 불일치, requester: {}, 업체 id: {}, 요청 companyId: {}",
                            command.getRequesterId(), company.getId(), command.getRequesterCompanyId());
                    throw new BusinessException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
                }
            }
            default -> {
                log.warn("업체 수정 권한 없음 - 허용되지 않는 역할, requester: {}, role: {}",
                        command.getRequesterId(), command.getRequesterRole());
                throw new BusinessException(CompanyErrorCode.COMPANY_ACCESS_DENIED);
            }
        }
    }
}