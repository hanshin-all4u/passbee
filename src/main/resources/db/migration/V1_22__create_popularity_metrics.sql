CREATE TABLE IF NOT EXISTS popularity_metrics (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  jmcd VARCHAR(20) NOT NULL,
  period DATE NOT NULL,                -- 해당 월의 1일(YYYY-MM-01)
  applications INT DEFAULT 0,
  pass_rate DECIMAL(5,4) DEFAULT 0,    -- 0~1
  favorites INT DEFAULT 0,
  reviews INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_pm (jmcd, period),
  INDEX idx_pm_period (period),
  INDEX idx_pm_jmcd (jmcd)
);