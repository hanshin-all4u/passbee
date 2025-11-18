CREATE TABLE IF NOT EXISTS license_relation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  src_jmcd VARCHAR(20) NOT NULL,
  dst_jmcd VARCHAR(20) NOT NULL,
  type ENUM('similar','higher','lower') NOT NULL,
  weight DECIMAL(5,4) DEFAULT 1.0,
  note VARCHAR(255) NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_rel (src_jmcd, dst_jmcd, type),
  INDEX idx_rel_src (src_jmcd, type)
);