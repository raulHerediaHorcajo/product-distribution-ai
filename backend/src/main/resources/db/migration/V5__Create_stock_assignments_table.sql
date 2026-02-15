CREATE TABLE stock_assignments (
    id UUID PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    warehouse_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    size VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    distance_km DOUBLE PRECISION,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE
);

CREATE INDEX idx_stock_assignments_store_id ON stock_assignments(store_id);
CREATE INDEX idx_stock_assignments_warehouse_id ON stock_assignments(warehouse_id);
CREATE INDEX idx_stock_assignments_product_id ON stock_assignments(product_id);

CREATE INDEX idx_stock_assignments_warehouse_store_product ON stock_assignments(warehouse_id, store_id, product_id);
CREATE INDEX idx_stock_assignments_warehouse_store_distance ON stock_assignments(warehouse_id, store_id, distance_km);