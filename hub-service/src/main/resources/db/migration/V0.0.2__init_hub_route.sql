-- 허브 경로 초기 데이터 삽입
-- distance, duration은 추후 외부 API를 통해 업데이트 예정 (임시값 0)
INSERT INTO hub_schema.p_hub_route (id, origin_hub_id, destination_hub_id, duration, distance, created_at, created_by)
SELECT
    gen_random_uuid(),
    o.id,
    d.id,
    0,
    0,
    NOW(),
    '00000000-0000-0000-0000-000000000000'
FROM (VALUES
    -- 경기 남부 센터 출발
    ('경기 남부 센터', '경기 북부 센터'),
    ('경기 남부 센터', '서울특별시 센터'),
    ('경기 남부 센터', '인천광역시 센터'),
    ('경기 남부 센터', '강원특별자치도 센터'),
    ('경기 남부 센터', '경상북도 센터'),
    ('경기 남부 센터', '대전광역시 센터'),
    ('경기 남부 센터', '대구광역시 센터'),
    -- 대전광역시 센터 출발
    ('대전광역시 센터', '충청남도 센터'),
    ('대전광역시 센터', '충청북도 센터'),
    ('대전광역시 센터', '세종특별자치시 센터'),
    ('대전광역시 센터', '전북특별자치도 센터'),
    ('대전광역시 센터', '광주광역시 센터'),
    ('대전광역시 센터', '전라남도 센터'),
    ('대전광역시 센터', '경기 남부 센터'),
    ('대전광역시 센터', '대구광역시 센터'),
    -- 대구광역시 센터 출발
    ('대구광역시 센터', '경상북도 센터'),
    ('대구광역시 센터', '경상남도 센터'),
    ('대구광역시 센터', '부산광역시 센터'),
    ('대구광역시 센터', '울산광역시 센터'),
    ('대구광역시 센터', '경기 남부 센터'),
    ('대구광역시 센터', '대전광역시 센터'),
    -- 경상북도 센터 출발
    ('경상북도 센터', '경기 남부 센터'),
    ('경상북도 센터', '대구광역시 센터')
) AS routes(origin_name, dest_name)
JOIN hub_schema.p_hub o ON o.name = routes.origin_name AND o.deleted_at IS NULL
JOIN hub_schema.p_hub d ON d.name = routes.dest_name AND d.deleted_at IS NULL;