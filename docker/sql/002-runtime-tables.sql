-- Additive local compatibility schema, reconstructed from repository consumers.
-- Original dump is kept unchanged. No historical data is claimed for these tables.
USE iemes_v1;

-- SpringTaskController persists these fields after sending an alert.
CREATE TABLE IF NOT EXISTS ly_server_info (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cpuUsage BIGINT,
    ramUsage BIGINT,
    jvmUsage BIGINT,
    setCpuUsage VARCHAR(32),
    setRamUsage VARCHAR(32),
    setJvmUsage VARCHAR(32),
    email VARCHAR(255),
    mark TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Existing /test and /test2 controllers and JSPs require these tables.
CREATE TABLE IF NOT EXISTS ly_jayce (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    byUser VARCHAR(255),
    createDate TIMESTAMP NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS ly_sukey LIKE ly_jayce;
