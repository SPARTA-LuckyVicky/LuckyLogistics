-- 기존에 user_id를 p_delivery_driver의 PK로 사용하던 방식에서,
-- id는 식별자로 두고, user_id를 별도로 두는 방식으로 변경
ALTER TABLE delivery_schema.p_delivery_driver
ADD COLUMN user_id UUID;

-- 기존 데이터가 있다면 id 값을 user_id로 복사
UPDATE delivery_schema.p_delivery_driver
SET user_id = id;

-- NOT NULL 제약 추가
ALTER TABLE delivery_schema.p_delivery_driver
ALTER COLUMN user_id SET NOT NULL;
