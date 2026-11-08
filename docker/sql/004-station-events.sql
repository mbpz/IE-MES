-- Reconstructed records for previously missing production-station pages.
USE iemes_v1;

CREATE TABLE IF NOT EXISTS ly_station_event (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    site VARCHAR(255) NOT NULL,
    station_type VARCHAR(32) NOT NULL,
    sfc VARCHAR(255) NOT NULL,
    result VARCHAR(16) NOT NULL,
    reference_no VARCHAR(80) DEFAULT NULL,
    note VARCHAR(500) DEFAULT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_station_site_type_id (site, station_type, id),
    KEY idx_station_site_sfc (site, sfc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO ly_resources (name, parentId, resKey, type, resUrl, level, ishide, description)
SELECT '维修工位', 39, 'nc_handle', '1', '/produce/repair_pod.shtml', 180, 0, '使用现有不良处置流程'
WHERE NOT EXISTS (SELECT 1 FROM ly_resources WHERE resUrl = '/produce/repair_pod.shtml');
INSERT INTO ly_resources (name, parentId, resKey, type, resUrl, level, ishide, description)
SELECT '包装工位', 39, 'package_pod', '1', '/produce/package_pod.shtml', 181, 0, '登记包装完成记录'
WHERE NOT EXISTS (SELECT 1 FROM ly_resources WHERE resUrl = '/produce/package_pod.shtml');
INSERT INTO ly_resources (name, parentId, resKey, type, resUrl, level, ishide, description)
SELECT '抽检工位', 39, 'sampling_pod', '1', '/produce/sampling_pod.shtml', 182, 0, '登记抽检结果'
WHERE NOT EXISTS (SELECT 1 FROM ly_resources WHERE resUrl = '/produce/sampling_pod.shtml');
INSERT INTO ly_resources (name, parentId, resKey, type, resUrl, level, ishide, description)
SELECT 'SFC工序查询', 39, 'sfc_step', '1', '/produce/sfc_step.shtml', 183, 0, '查询已有工序记录'
WHERE NOT EXISTS (SELECT 1 FROM ly_resources WHERE resUrl = '/produce/sfc_step.shtml');
INSERT INTO ly_resources (name, parentId, resKey, type, resUrl, level, ishide, description)
SELECT '设备测试工位', 39, 'equipment_testing_pod', '1', '/produce/equipment_testing_pod.shtml', 184, 0, '登记设备测试结果'
WHERE NOT EXISTS (SELECT 1 FROM ly_resources WHERE resUrl = '/produce/equipment_testing_pod.shtml');
INSERT INTO ly_resources (name, parentId, resKey, type, resUrl, level, ishide, description)
SELECT '外观检验工位', 39, 'visual_inspection_pod', '1', '/produce/visual_inspection_pod.shtml', 185, 0, '登记外观检验结果'
WHERE NOT EXISTS (SELECT 1 FROM ly_resources WHERE resUrl = '/produce/visual_inspection_pod.shtml');

INSERT IGNORE INTO ly_res_role (resId, roleId)
SELECT id, 1 FROM ly_resources
WHERE resUrl IN ('/produce/repair_pod.shtml', '/produce/package_pod.shtml',
                 '/produce/sampling_pod.shtml', '/produce/sfc_step.shtml',
                 '/produce/equipment_testing_pod.shtml', '/produce/visual_inspection_pod.shtml');
