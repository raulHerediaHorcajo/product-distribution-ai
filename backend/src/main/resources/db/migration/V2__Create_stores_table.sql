CREATE TABLE stores (
    id VARCHAR(255) PRIMARY KEY,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    country VARCHAR(255),
    max_stock_capacity INTEGER,
    expected_return_rate DOUBLE PRECISION,
    remaining_capacity INTEGER
);