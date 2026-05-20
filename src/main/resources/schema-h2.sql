-- ================================
--  index_info (지수 정보)
-- ================================
CREATE TABLE IF NOT EXISTS index_info (
                                          id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,

    index_name VARCHAR(100) NOT NULL,
    index_classification VARCHAR(50) NOT NULL,
    employed_items_count INTEGER NOT NULL,

    base_point_in_time DATE NOT NULL,
    base_index NUMERIC(20,2) NOT NULL,

    source_type VARCHAR(20) NOT NULL CHECK (source_type IN ('USER','OPEN_API')),
    favorite BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    UNIQUE (index_classification, index_name)
    );

-- 즐겨찾기 조회 인덱스
CREATE INDEX IF NOT EXISTS idx_index_info_favorite
    ON index_info (favorite);


-- ================================
--  index_data (지수 데이터)
-- ================================
CREATE TABLE IF NOT EXISTS index_data (
                                          id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,

    index_id UUID NOT NULL,

    base_date DATE NOT NULL,
    source_type VARCHAR(20) NOT NULL CHECK (source_type IN ('USER','OPEN_API')),

    market_price NUMERIC(20,2) NOT NULL,
    closing_price NUMERIC(20,2) NOT NULL,
    high_price NUMERIC(20,2) NOT NULL,
    low_price NUMERIC(20,2) NOT NULL,

    versus NUMERIC(20,2) NOT NULL,
    fluctuation_rate NUMERIC(20,2) NOT NULL,

    trade_quantity BIGINT NOT NULL,
    trade_price BIGINT NOT NULL,
    market_total_amount BIGINT NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    UNIQUE (index_id, base_date),

    FOREIGN KEY (index_id)
    REFERENCES index_info(id)
    ON DELETE CASCADE
    );

-- 최신 데이터 조회용
CREATE INDEX IF NOT EXISTS idx_index_data_lookup
    ON index_data (index_id, base_date DESC);


-- ================================
--  auto_sync
-- ================================
CREATE TABLE IF NOT EXISTS auto_sync (
                                         id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,

    index_id UUID NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    FOREIGN KEY (index_id)
    REFERENCES index_info(id)
    ON DELETE CASCADE
    );


-- ================================
--  sync_info (동기화 기록)
-- ================================
CREATE TABLE IF NOT EXISTS sync_info (
                                         id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,

    index_id UUID NOT NULL,
    job_type VARCHAR(20),
    target_date DATE,

    worker VARCHAR(20),
    job_time TIMESTAMP,
    result VARCHAR(20),

    FOREIGN KEY (index_id)
    REFERENCES index_info(id)
    ON DELETE CASCADE
    );

CREATE INDEX IF NOT EXISTS idx_sync_info_lookup
    ON sync_info (index_id, target_date DESC);