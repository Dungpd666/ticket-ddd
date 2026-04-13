USE vetautet;

-- ============================================================
-- NEW TABLES
-- ============================================================

CREATE TABLE IF NOT EXISTS station (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100) NOT NULL,
    code       VARCHAR(10)  NOT NULL COMMENT 'Short code e.g. HAN, SGN',
    city       VARCHAR(100),
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_station_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Train stations';

CREATE TABLE IF NOT EXISTS route (
    id                BIGINT NOT NULL AUTO_INCREMENT,
    origin_station_id BIGINT NOT NULL,
    dest_station_id   BIGINT NOT NULL,
    distance_km       INT,
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_route_origin FOREIGN KEY (origin_station_id) REFERENCES station(id),
    CONSTRAINT fk_route_dest   FOREIGN KEY (dest_station_id)   REFERENCES station(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Train routes between stations';

CREATE TABLE IF NOT EXISTS train (
    id           BIGINT      NOT NULL AUTO_INCREMENT,
    train_number VARCHAR(20) NOT NULL COMMENT 'e.g. SE1, SE2, TN1',
    train_type   VARCHAR(20) NOT NULL COMMENT 'EXPRESS or LOCAL',
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_train_number (train_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Trains';

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT 'USER or ADMIN',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_username (username),
    UNIQUE KEY uq_users_email    (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Application users';

CREATE TABLE IF NOT EXISTS payment (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    order_id     BIGINT       NOT NULL,
    amount       BIGINT       NOT NULL COMMENT 'Amount in VND (smallest unit)',
    provider     VARCHAR(20)  NOT NULL DEFAULT 'VNPAY',
    txn_ref      VARCHAR(100) NOT NULL COMMENT 'Our reference sent to VNPay (= orderId)',
    status       INT          NOT NULL DEFAULT 0 COMMENT '0=PENDING 1=SUCCESS 2=FAILED',
    vnpay_txn_no VARCHAR(100) COMMENT 'Transaction number returned by VNPay',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_payment_order   (order_id),
    UNIQUE KEY uq_payment_txn_ref (txn_ref),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Payment records';

-- ============================================================
-- EXTEND ticket → train_trip
-- Each ALTER is guarded by an information_schema check so the
-- script is safe to re-run (idempotent).
-- ============================================================

SET @db = DATABASE();

-- Add route_id
SET @q = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ticket' AND COLUMN_NAME='route_id') = 0,
    'ALTER TABLE ticket ADD COLUMN route_id BIGINT COMMENT ''FK to route''',
    'SELECT 1 -- route_id already exists'
);
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- Add train_id
SET @q = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ticket' AND COLUMN_NAME='train_id') = 0,
    'ALTER TABLE ticket ADD COLUMN train_id BIGINT COMMENT ''FK to train''',
    'SELECT 1 -- train_id already exists'
);
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- Add origin
SET @q = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ticket' AND COLUMN_NAME='origin') = 0,
    'ALTER TABLE ticket ADD COLUMN origin VARCHAR(100) COMMENT ''Origin station name''',
    'SELECT 1 -- origin already exists'
);
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- Add destination
SET @q = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ticket' AND COLUMN_NAME='destination') = 0,
    'ALTER TABLE ticket ADD COLUMN destination VARCHAR(100) COMMENT ''Destination station name''',
    'SELECT 1 -- destination already exists'
);
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- Rename start_time → departure_time
SET @q = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ticket' AND COLUMN_NAME='start_time') > 0,
    'ALTER TABLE ticket RENAME COLUMN start_time TO departure_time',
    'SELECT 1 -- already renamed'
);
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- Rename end_time → arrival_time
SET @q = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ticket' AND COLUMN_NAME='end_time') > 0,
    'ALTER TABLE ticket RENAME COLUMN end_time TO arrival_time',
    'SELECT 1 -- already renamed'
);
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- Rename table ticket → train_trip
SET @q = IF(
    (SELECT COUNT(*) FROM information_schema.TABLES
     WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ticket') > 0,
    'RENAME TABLE ticket TO train_trip',
    'SELECT 1 -- already renamed'
);
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- ============================================================
-- EXTEND ticket_item → seat_class
-- ============================================================

-- Rename activity_id → trip_id
SET @q = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ticket_item' AND COLUMN_NAME='activity_id') > 0,
    'ALTER TABLE ticket_item RENAME COLUMN activity_id TO trip_id',
    'SELECT 1 -- already renamed'
);
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- Rename price_original → price
SET @q = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ticket_item' AND COLUMN_NAME='price_original') > 0,
    'ALTER TABLE ticket_item RENAME COLUMN price_original TO price',
    'SELECT 1 -- already renamed'
);
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- Rename table ticket_item → seat_class
SET @q = IF(
    (SELECT COUNT(*) FROM information_schema.TABLES
     WHERE TABLE_SCHEMA=@db AND TABLE_NAME='ticket_item') > 0,
    'RENAME TABLE ticket_item TO seat_class',
    'SELECT 1 -- already renamed'
);
PREPARE s FROM @q; EXECUTE s; DEALLOCATE PREPARE s;

-- ============================================================
-- SEED DATA
-- INSERT IGNORE makes this safe to re-run
-- ============================================================

INSERT IGNORE INTO station (name, code, city) VALUES
    ('Ga Hà Nội',    'HAN', 'Hà Nội'),
    ('Ga Đà Nẵng',   'DAN', 'Đà Nẵng'),
    ('Ga Sài Gòn',   'SGN', 'TP.HCM'),
    ('Ga Nha Trang', 'NHA', 'Nha Trang'),
    ('Ga Huế',       'HUE', 'Huế');

INSERT IGNORE INTO train (train_number, train_type) VALUES
    ('SE1', 'EXPRESS'),
    ('SE2', 'EXPRESS'),
    ('SE3', 'EXPRESS'),
    ('TN1', 'LOCAL'),
    ('TN2', 'LOCAL');
