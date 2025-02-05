CREATE TABLE IF NOT EXISTS item_orders (
    id INT(11) NOT NULL AUTO_INCREMENT,
    product_id INT(11) NOT NULL,
    quantity INT NOT NULL,
    sub_total DECIMAL(10,2) NOT NULL,
     order_id BIGINT NOT NULL,
    CONSTRAINT fk_item_order_productId FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_item_order_order FOREIGN KEY (order_id) REFERENCES orders(id),
    PRIMARY KEY (id)
) ENGINE=InnoDB;