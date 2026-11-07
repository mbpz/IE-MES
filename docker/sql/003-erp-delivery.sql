-- Reconstructed local ERP-order and delivery ledgers. No historical ERP integration is implied.
USE iemes_v1;

CREATE TABLE IF NOT EXISTS ly_erp_order (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    site VARCHAR(255) NOT NULL,
    erp_order_no VARCHAR(80) NOT NULL,
    customer_name VARCHAR(255) DEFAULT NULL,
    item_no VARCHAR(255) NOT NULL,
    ordered_quantity DECIMAL(18,3) NOT NULL,
    expected_delivery_date DATE DEFAULT NULL,
    shoporder_id INT DEFAULT NULL,
    note VARCHAR(500) DEFAULT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_erp_order_site_no (site, erp_order_no),
    KEY idx_erp_order_site_id (site, id),
    KEY idx_erp_order_shoporder (shoporder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS ly_delivery_record (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    site VARCHAR(255) NOT NULL,
    shoporder_id INT NOT NULL,
    quantity DECIMAL(18,3) NOT NULL,
    reference_no VARCHAR(80) DEFAULT NULL,
    note VARCHAR(500) DEFAULT NULL,
    created_by VARCHAR(255) NOT NULL,
    delivered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_delivery_site_id (site, id),
    KEY idx_delivery_shoporder (shoporder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO ly_resources (name, parentId, resKey, type, resUrl, level, ishide, description)
SELECT 'ERP订单登记', 38, 'erp_order', '1', '/plan/erp_order.shtml', 170, 0, '手工登记外部订单'
WHERE NOT EXISTS (SELECT 1 FROM ly_resources WHERE resKey = 'erp_order' AND resUrl = '/plan/erp_order.shtml');

INSERT INTO ly_resources (name, parentId, resKey, type, resUrl, level, ishide, description)
SELECT '交付数量登记', 38, 'deliver_number', '1', '/plan/deliver_number.shtml', 171, 0, '登记工单交付数量，不扣库存'
WHERE NOT EXISTS (SELECT 1 FROM ly_resources WHERE resKey = 'deliver_number' AND resUrl = '/plan/deliver_number.shtml');

INSERT IGNORE INTO ly_res_role (resId, roleId)
SELECT id, 1 FROM ly_resources
WHERE (resKey = 'erp_order' AND resUrl = '/plan/erp_order.shtml')
   OR (resKey = 'deliver_number' AND resUrl = '/plan/deliver_number.shtml');
