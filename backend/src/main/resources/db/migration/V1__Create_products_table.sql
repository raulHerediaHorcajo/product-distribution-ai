CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY,
    brand_id VARCHAR(255) NOT NULL
);

CREATE TABLE product_sizes (
    product_id VARCHAR(255) NOT NULL,
    sizes VARCHAR(255) NOT NULL,
    PRIMARY KEY (product_id, sizes),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);