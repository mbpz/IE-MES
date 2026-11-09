-- Prevent duplicate site codes; existing databases with duplicates must resolve them before this migration.
USE iemes_v1;

SET @site_unique_exists = (
    SELECT COUNT(*) FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'ly_site' AND index_name = 'uk_ly_site_site'
);
SET @site_unique_sql = IF(@site_unique_exists = 0,
    'ALTER TABLE ly_site ADD UNIQUE KEY uk_ly_site_site (site)',
    'SELECT 1');
PREPARE site_unique_statement FROM @site_unique_sql;
EXECUTE site_unique_statement;
DEALLOCATE PREPARE site_unique_statement;
