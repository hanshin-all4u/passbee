CREATE TABLE IF NOT EXISTS user_license (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id        BIGINT       NOT NULL,
  jmcd           VARCHAR(32)  NOT NULL,
  obtained_date  DATE         NOT NULL,
  level_grade    VARCHAR(32),
  score          DECIMAL(5,2),
  certificate_no VARCHAR(64),
  issuer         VARCHAR(64)  DEFAULT 'Q-NET',
  expires_at     DATE,
  memo           VARCHAR(255),
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uk_user_license UNIQUE (user_id, jmcd, obtained_date),
  INDEX idx_user_license_user (user_id),
  INDEX idx_user_license_jmcd (jmcd)
);