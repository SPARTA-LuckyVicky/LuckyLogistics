package com.sparta.lucky.company.infrastructure;

import com.sparta.lucky.company.domain.Company;
import com.sparta.lucky.company.domain.CompanyType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CompanyJpaRepository companyJpaRepository;

    private static final UUID SYSTEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private static final List<String> HUB_IDS = List.of(
            "11111111-1111-1111-1111-111111111111",
            "22222222-2222-2222-2222-222222222222",
            "33333333-3333-3333-3333-333333333333"
    );

    private static final List<String> NAME_PREFIXES = List.of(
            "서울", "경기", "부산", "대구", "인천", "광주", "대전"
    );

    private static final List<String> NAME_SUFFIXES = List.of(
            "건조식품", "냉동물류", "신선식품", "전자부품", "의류유통", "화학소재"
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 이미 데이터 있으면 스킵
        if (companyJpaRepository.count() > 0) return;

        Random random = new Random();
        List<Company> companies = new ArrayList<>();

        for (int i = 0; i < 300; i++) {
            String prefix = NAME_PREFIXES.get(random.nextInt(NAME_PREFIXES.size()));
            String suffix = NAME_SUFFIXES.get(random.nextInt(NAME_SUFFIXES.size()));
            CompanyType type = random.nextBoolean() ? CompanyType.SUPPLIER : CompanyType.RECEIVER;
            UUID hubId = UUID.fromString(HUB_IDS.get(random.nextInt(HUB_IDS.size())));

            companies.add(Company.builder()
                    .name(prefix + " " + suffix + " " + (i + 1) + "호")
                    .companyType(type)
                    .hubId(hubId)
                    .address(prefix + "시 테스트로 " + (i + 1) + "번길")
                    .build());
        }

        companyJpaRepository.saveAll(companies);
        System.out.println("DataInitializer: 업체 300개 생성 완료");
    }
}