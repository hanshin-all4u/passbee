CREATE TABLE IF NOT EXISTS exam_schedule (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  jmcd         VARCHAR(32) NOT NULL,
  date         DATE        NOT NULL,
  type         ENUM('REG_OPEN','REG_CLOSE','PI_EXAM','SI_EXAM','RESULT') NOT NULL,
  apply_url    VARCHAR(512),
  note         VARCHAR(255),
  source       ENUM('QNET','MANUAL') NOT NULL DEFAULT 'QNET',
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_exam_schedule_jmcd_date_type (jmcd, date, type),
  KEY idx_exam_schedule_date (date),
  KEY idx_exam_schedule_jmcd_date (jmcd, date),
  KEY idx_exam_schedule_type_date (type, date)
);