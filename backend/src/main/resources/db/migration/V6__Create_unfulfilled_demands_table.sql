CREATE TABLE unfulfilled_demands (
    id UUID PRIMARY KEY,
    store_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    size VARCHAR(255) NOT NULL,
    quantity_missing INTEGER NOT NULL,
    reason VARCHAR(50) NOT NULL,
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE
);

CREATE INDEX idx_unfulfilled_demands_store_id ON unfulfilled_demands(store_id);
CREATE INDEX idx_unfulfilled_demands_product_id ON unfulfilled_demands(product_id);

CREATE INDEX idx_unfulfilled_demands_store_product ON unfulfilled_demands(store_id, product_id);