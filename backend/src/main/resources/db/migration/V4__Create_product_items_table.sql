CREATE TABLE product_items (
    id UUID PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    size VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    store_id VARCHAR(255),
    warehouse_id VARCHAR(255),
    FOREIGN KEY (store_id) REFERENCES stores(id) ON DELETE CASCADE,
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(id) ON DELETE CASCADE
);

CREATE INDEX idx_product_items_store_id ON product_items(store_id);
CREATE INDEX idx_product_items_warehouse_id ON product_items(warehouse_id);
CREATE INDEX idx_product_items_product_size ON product_items(product_id, size);