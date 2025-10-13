CREATE TABLE IF NOT EXISTS favorites (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  target_type VARCHAR(32) NOT NULL,
  target_id BIGINT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 동일 유저가 같은 대상(type+id)을 1번만 즐겨찾기
CREATE UNIQUE INDEX IF NOT EXISTS ux_favorites_user_target
  ON favorites(user_id, target_type, target_id);

CREATE INDEX IF NOT EXISTS ix_favorites_user_type
  ON favorites(user_id, target_type);
