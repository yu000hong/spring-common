CREATE TABLE IF NOT EXISTS "account" (
  "uid"         BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
  "nickname"    VARCHAR(20)  NOT NULL,
  "code"        VARCHAR(40)  NULL,
  "image"       VARCHAR(500) NOT NULL,
  "create_time" TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS "idx_nickname" ON "account" ("nickname");



