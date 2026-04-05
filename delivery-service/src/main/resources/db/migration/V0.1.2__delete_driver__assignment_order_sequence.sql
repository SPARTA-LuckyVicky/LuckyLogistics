-- 배송 생성시 배송담당자의 순차배정을 위해 round robin 알고리즘을 적용하게 되어, 적용되어있던 시퀀스 제거
-- 적용된 시퀀스 초기화
ALTER TABLE delivery_schema.p_delivery_driver
  ALTER COLUMN assignment_order DROP DEFAULT;

-- 시퀀스 제거
DROP SEQUENCE IF EXISTS delivery_schema.driver_assignment_order_seq;