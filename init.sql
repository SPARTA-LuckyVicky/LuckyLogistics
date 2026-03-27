-- 스키마 생성
CREATE SCHEMA IF NOT EXISTS user_schema;
CREATE SCHEMA IF NOT EXISTS hub_schema;
CREATE SCHEMA IF NOT EXISTS company_schema;
CREATE SCHEMA IF NOT EXISTS product_schema;
CREATE SCHEMA IF NOT EXISTS order_schema;
CREATE SCHEMA IF NOT EXISTS delivery_schema;
CREATE SCHEMA IF NOT EXISTS notification_schema;

-- 권한 부여
GRANT ALL ON SCHEMA user_schema TO lucky;
GRANT ALL ON SCHEMA hub_schema TO lucky;
GRANT ALL ON SCHEMA company_schema TO lucky;
GRANT ALL ON SCHEMA product_schema TO lucky;
GRANT ALL ON SCHEMA order_schema TO lucky;
GRANT ALL ON SCHEMA delivery_schema TO lucky;
GRANT ALL ON SCHEMA notification_schema TO lucky;

