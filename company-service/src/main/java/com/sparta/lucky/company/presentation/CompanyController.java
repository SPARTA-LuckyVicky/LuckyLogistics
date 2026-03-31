package com.sparta.lucky.company.presentation;

import com.sparta.lucky.company.application.CompanyService;
import com.sparta.lucky.company.application.dto.CreateCompanyCommand;
import com.sparta.lucky.company.application.dto.UpdateCompanyCommand;
import com.sparta.lucky.company.common.response.ApiResponse;
import com.sparta.lucky.company.domain.CompanyType;
import com.sparta.lucky.company.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 외부 API 담당 컨트롤러
 * @RequestHeader("X-User-Id") UUID userId : Gateway가 JWt에서 파싱한 요청자 UUID
 * @RequestHeader("X-User-Role") String userRole : JWT 파싱한 요청자 role
 * @RequestHeader("X-Hub-Id") UUID hubId : JWT 파싱 요청자 hubId
 * @RequestHeader("X-Company-Id") UUID companyId : JWT 파싱 요청자 companyId
 */

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    /**
     * 업체 생성
     * [권한]
     * - MASTER는 모든 허브 업체 생성 가능
     * - HUB_MANAGER는 자신의 소속 허브 업체만 생성 가능
     *
     * @param reqDto 업체 생성 요청 (name, companyType, hubId, address)
     * @param userId 요청자 UUID
     * @param userRole 요청자 role
     * @param hubId HUB_MANAGER의 담당 허브 UUID (MASTER는 null)
     * @return 생성된 업체 데이터
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostCompanyResDto> createCompany(
            @Valid @RequestBody PostCompanyReqDto reqDto,
            // Gateway가 JWT 파싱 후 주입하는 헤더들
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        // command로 감싸서 service에 전달 (Http 종속성 제거)
        CreateCompanyCommand command = CreateCompanyCommand.builder()
                .name(reqDto.getName())
                .companyType(reqDto.getCompanyType())
                .hubId(reqDto.getHubId())
                .address(reqDto.getAddress())
                .requesterId(userId)
                .requesterRole(userRole)
                .requesterHubId(hubId)
                .build();

        return ApiResponse.success(
                PostCompanyResDto.from(companyService.createCompany(command))
        );
    }


    /**
     * 업체 목록 조회 / 검색
     * [권한]
     * - 모든 로그인 사용자 가능
     *
     * @param name 업체명 검색어 (생략시 전체 조회)
     * @param type 업체유형 (SUPPLIER / RECEIVER)
     * @param hubId 업체 소속 허브 UUID
     * @param pageable 페이지네이션 (기본값 size = 10, createdAt DESC)
     * @return 업체 목록 페이지
     */
    @GetMapping
    public ApiResponse<Page<GetCompanyResDto>> getCompanies(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) CompanyType type,
            @RequestParam(required = false) UUID hubId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // size 유효성 검사 : 10/30/50 외 값은 10으로 고정
        int validSize = List.of(10,30,50).contains(pageable.getPageSize())
                ? pageable.getPageSize() : 10;

        Pageable validPageable = PageRequest.of(
                pageable.getPageNumber(), validSize, pageable.getSort()
        );

        return ApiResponse.success(
                companyService.getCompanies(name, type, hubId, validPageable).map(GetCompanyResDto::from)
        );
    }

    /**
     * 업체 단건 조회
     * [권한]
     * - 모든 로그인 사용자 가능
     * @param companyId
     * @return
     */
    @GetMapping("/{companyId}")
    public ApiResponse<GetCompanyResDto> getCompany(@PathVariable UUID companyId) {
        return ApiResponse.success(
                GetCompanyResDto.from(companyService.getCompany(companyId))
        );
    }

    /**
     * 업체 수정
     * [권한]
     * MASTER - 모두 가능
     * HUB_MANAGER - 담당 허브 업체만 가능
     * COMPANY-MANAGER - 본인 업체만 가능
     *
     * @param companyId
     * @param reqDto 업체 수정 요청
     * @param userId
     * @param userRole
     * @param hubId
     * @param companyIdHeader
     * @return 수정된 업체 결과
     */
    @PatchMapping("/{companyId}")
    public ApiResponse<GetCompanyResDto> updateCompany(
            @PathVariable UUID companyId,
            @Valid @RequestBody PatchCompanyReqDto reqDto,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId,
            @RequestHeader(value = "X-Company-Id", required = false) UUID companyIdHeader
    ) {
        UpdateCompanyCommand command = UpdateCompanyCommand.builder()
                .companyId(companyId)
                .name(reqDto.getName())
                .companyType(reqDto.getCompanyType())
                .hubId(reqDto.getHubId())
                .address(reqDto.getAddress())
                .requesterId(userId)
                .requesterRole(userRole)
                .requesterHubId(hubId)
                .requesterCompanyId(companyIdHeader)
                .build();

        return ApiResponse.success(
                GetCompanyResDto.from(companyService.updateCompany(command))
        );
    }

    /**
     * 업제 삭제
     * [권한]
     * MASTER - 모두 가능
     * HUB_MANAGER - 담당 허브 업체만 가능
     * @param companyId
     * @param userId
     * @param userRole
     * @param hubId
     * @return 삭제 결과
     */
    @DeleteMapping("/{companyId}")
    public ApiResponse<DeleteCompanyResDto> deleteCompany(
            @PathVariable UUID companyId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        return ApiResponse.success(
                DeleteCompanyResDto.from(
                        companyService.deleteCompany(companyId, userId, userRole, hubId)
                )
        );
    }
}