CREATE TABLE IF NOT EXISTS tb_user (
    user_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(255),
    address VARCHAR(255),
    role VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tb_order (
    order_id VARCHAR(36) PRIMARY KEY,
    created_at DATE,
    status VARCHAR(255),
    total_amount DOUBLE,
    discount DOUBLE,
    notes VARCHAR(255),
    user_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES tb_user(user_id)
);

CREATE TABLE IF NOT EXISTS tb_product (
    product_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255),
    category VARCHAR(255),
    color VARCHAR(255),
    size VARCHAR(255),
    price DOUBLE,
    active BOOLEAN
);

CREATE TABLE IF NOT EXISTS tb_installment_payment (
    installment_payment_id VARCHAR(36) PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_id VARCHAR(36),
    installment_number INT,
    amount DOUBLE,
    maturity DATE,
    paid BOOLEAN,
    payment_date DATE,
    method VARCHAR(255),
    CONSTRAINT fk_installment_order FOREIGN KEY (order_id) REFERENCES tb_order(order_id)
);

CREATE TABLE IF NOT EXISTS tb_order_item (
    order_item_id VARCHAR(36) PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity BIGINT,
    unity_price DOUBLE,
    subtotal DOUBLE,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES tb_order(order_id),
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES tb_product(product_id)
);

CREATE TABLE IF NOT EXISTS tb_inventory_movement (
    inventory_movement_id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    type VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    date DATE NOT NULL,
    reason VARCHAR(255),
    CONSTRAINT fk_inventory_movement_product FOREIGN KEY (product_id) REFERENCES tb_product(product_id),
    CONSTRAINT chk_movement_type CHECK (type IN ('IN', 'OUT'))
);
