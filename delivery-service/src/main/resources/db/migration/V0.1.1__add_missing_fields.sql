-- delivery driver
ALTER TABLE delivery_schema.p_delivery_driver
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN created_by UUID,
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN updated_by UUID,
ADD COLUMN deleted_at TIMESTAMP,
ADD COLUMN deleted_by UUID;

UPDATE delivery_schema.p_delivery_driver
SET
created_at = now(),
created_by = '00000000-0000-0000-0000-000000000000',
updated_at = now(),
updated_by = '00000000-0000-0000-0000-000000000000';

ALTER TABLE delivery_schema.p_delivery_driver
ALTER COLUMN created_at SET NOT NULL,
ALTER COLUMN created_by SET NOT NULL;

-- delivery
ALTER TABLE delivery_schema.p_delivery
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN created_by UUID,
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN updated_by UUID,
ADD COLUMN deleted_at TIMESTAMP,
ADD COLUMN deleted_by UUID;

UPDATE delivery_schema.p_delivery
SET
created_at = now(),
created_by = '00000000-0000-0000-0000-000000000000',
updated_at = now(),
updated_by = '00000000-0000-0000-0000-000000000000';

ALTER TABLE delivery_schema.p_delivery
ALTER COLUMN created_at SET NOT NULL,
ALTER COLUMN created_by SET NOT NULL;

-- delivery route
ALTER TABLE delivery_schema.p_delivery_route
ADD COLUMN created_at TIMESTAMP,
ADD COLUMN created_by UUID,
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN updated_by UUID,
ADD COLUMN deleted_at TIMESTAMP,
ADD COLUMN deleted_by UUID;

UPDATE delivery_schema.p_delivery_route
SET
created_at = now(),
created_by = '00000000-0000-0000-0000-000000000000',
updated_at = now(),
updated_by = '00000000-0000-0000-0000-000000000000';

ALTER TABLE delivery_schema.p_delivery_route
ALTER COLUMN created_at SET NOT NULL,
ALTER COLUMN created_by SET NOT NULL;


