CREATE TABLE IF NOT EXISTS refresh_token (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT      NOT NULL,
    token        VARCHAR(255) NOT NULL,
    issued_at    DATETIME     NOT NULL,
    expires_at   DATETIME     NOT NULL,
    user_agent   VARCHAR(255),
    ip_address   VARCHAR(64),
    revoked      TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT uk_refresh_token UNIQUE (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- idx_refresh_user (없을 때만 생성)
SET @idx_exists := (
   SELECT COUNT(*) FROM information_schema.statistics
   WHERE table_schema = DATABASE()
     AND table_name   = 'refresh_token'
     AND index_name   = 'idx_refresh_user'
);
SET @sql := IF(@idx_exists = 0,
   'CREATE INDEX idx_refresh_user ON refresh_token(user_id)',
   'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- === Verification Token ===
CREATE TABLE IF NOT EXISTS verification_token (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT       NOT NULL,
    type         VARCHAR(30)  NOT NULL,      -- EMAIL_VERIFY / PASSWORD_RESET 등
    token        VARCHAR(255) NOT NULL,
    created_at   DATETIME     NOT NULL,
    expires_at   DATETIME     NOT NULL,
    used         TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT uk_verification_token UNIQUE (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- idx_verif_user (없을 때만 생성)
SET @idx2_exists := (
   SELECT COUNT(*) FROM information_schema.statistics
   WHERE table_schema = DATABASE()
     AND table_name   = 'verification_token'
     AND index_name   = 'idx_verif_user'
);
SET @sql2 := IF(@idx2_exists = 0,
   'CREATE INDEX idx_verif_user ON verification_token(user_id)',
   'SELECT 1'
);
PREPARE stmt2 FROM @sql2; EXECUTE stmt2; DEALLOCATE PREPARE stmt2;