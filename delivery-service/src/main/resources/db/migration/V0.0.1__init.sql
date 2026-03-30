-- delivery driver assignment order용 시퀀스
CREATE SEQUENCE delivery_schema.driver_assignment_order_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- 배송 기사
CREATE TABLE delivery_schema.p_delivery_driver (
    id UUID PRIMARY KEY,
    hub_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    assignment_order INTEGER NOT NULL DEFAULT nextval('delivery_schema.driver_assignment_order_seq'),
    status VARCHAR(50) NOT NULL
);

-- 배송
CREATE TABLE delivery_schema.p_delivery (
    id UUID PRIMARY KEY,
    company_driver_id UUID,
    order_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    origin_hub UUID NOT NULL,
    current_hub UUID,
    destination_hub UUID NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    recipient_slack_id VARCHAR(100) NOT NULL,
    started_at TIMESTAMP,
    arrived_at TIMESTAMP,
    CONSTRAINT fk_delivery_company_driver
        FOREIGN KEY (company_driver_id)
        REFERENCES delivery_schema.p_delivery_driver(id)
);

-- 배송 경로
CREATE TABLE delivery_schema.p_delivery_route (
    id UUID PRIMARY KEY,
    delivery_id UUID NOT NULL,
    hub_driver_id UUID NOT NULL,
    sequence INTEGER NOT NULL,
    from_hub_id UUID NOT NULL,
    to_hub_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    expected_distance BIGINT NOT NULL,
    expected_duration_seconds BIGINT NOT NULL,
    actual_distance BIGINT,
    actual_duration_seconds BIGINT,
    started_at TIMESTAMP,
    arrived_at TIMESTAMP,
    CONSTRAINT fk_delivery_route_delivery
        FOREIGN KEY (delivery_id)
        REFERENCES delivery_schema.p_delivery(id),
    CONSTRAINT fk_delivery_route_hub_driver
        FOREIGN KEY (hub_driver_id)
        REFERENCES delivery_schema.p_delivery_driver(id)
);

CREATE INDEX idx_delivery_order_id
    ON delivery_schema.p_delivery(order_id);

CREATE INDEX idx_delivery_company_driver_id
    ON delivery_schema.p_delivery(company_driver_id);

CREATE INDEX idx_delivery_route_delivery_id
    ON delivery_schema.p_delivery_route(delivery_id);

CREATE INDEX idx_delivery_route_hub_driver_id
    ON delivery_schema.p_delivery_route(hub_driver_id);

CREATE INDEX idx_delivery_driver_hub_id
    ON delivery_schema.p_delivery_driver(hub_id);