-- ================================
--  DB 초기 구조 생성 (팀 공통 사용)
-- ⚠ DROP TABLE 없음 (데이터 보호)
-- ================================


-- ================================
--  index_info (지수 정보)
-- ================================
CREATE TABLE IF NOT EXISTS index_info (
                                          id                   UUID PRIMARY KEY,                -- 지수 ID
                                          index_name           VARCHAR(100) NOT NULL,           -- 지수 이름
    index_classification VARCHAR(50)  NOT NULL,           -- 지수 분류
    employed_items_count INTEGER      NOT NULL,           -- 포함 종목 수
    base_point_in_time   DATE         NOT NULL,           -- 기준 시점
    base_index           NUMERIC(20, 2) NOT NULL,         -- 기준 지수 값
    source_type          VARCHAR(20)  NOT NULL CHECK (source_type IN ('USER', 'OPEN_API')), -- 데이터 출처
    favorite             BOOLEAN      NOT NULL DEFAULT FALSE, -- 즐겨찾기 여부
    created_at           TIMESTAMPTZ  NOT NULL,           -- 생성일
    updated_at           TIMESTAMPTZ  NOT NULL,           -- 수정일

-- 같은 분류 + 이름 중복 방지
    UNIQUE (index_classification, index_name)
    );

-- 즐겨찾기 조회 최적화 인덱스
CREATE INDEX IF NOT EXISTS idx_index_info_favorite_true
    ON index_info (favorite)
    WHERE favorite = TRUE;



-- ================================
--  auto_sync (자동 동기화 설정)
-- ================================
CREATE TABLE IF NOT EXISTS auto_sync (
                                         id         UUID PRIMARY KEY,
                                         index_id   UUID        NOT NULL UNIQUE,  -- index_info 참조
                                         enabled    BOOLEAN     NOT NULL DEFAULT FALSE, -- 자동 동기화 여부
                                         created_at TIMESTAMPTZ NOT NULL,
                                         updated_at TIMESTAMPTZ NOT NULL,

                                         CONSTRAINT fk_syncauto_index
                                         FOREIGN KEY (index_id)
    REFERENCES index_info (id)
    ON DELETE CASCADE
    );



-- ================================
--  index_data (지수 데이터)
-- ================================
CREATE TABLE IF NOT EXISTS index_data (
                                          id                  UUID PRIMARY KEY,
                                          index_id            UUID NOT NULL, -- index_info 참조
                                          base_date           DATE NOT NULL, -- 날짜
                                          source_type         VARCHAR(20) NOT NULL CHECK (source_type IN ('USER', 'OPEN_API')),

    market_price        NUMERIC(20, 2) NOT NULL, -- 시가총액
    closing_price       NUMERIC(20, 2) NOT NULL, -- 종가
    high_price          NUMERIC(20, 2) NOT NULL, -- 최고가
    low_price           NUMERIC(20, 2) NOT NULL, -- 최저가
    versus              NUMERIC(20, 2) NOT NULL, -- 전일 대비
    fluctuation_rate    NUMERIC(20, 2) NOT NULL, -- 등락률

    trade_quantity      BIGINT NOT NULL, -- 거래량
    trade_price         BIGINT NOT NULL, -- 거래대금
    market_total_amount BIGINT NOT NULL, -- 시가총액 합

    created_at          TIMESTAMPTZ NOT NULL,
    updated_at          TIMESTAMPTZ NOT NULL,

    -- 같은 날짜 데이터 중복 방지
    CONSTRAINT uq_index_data_index_date
    UNIQUE (index_id, base_date),

    CONSTRAINT fk_data_index
    FOREIGN KEY (index_id)
    REFERENCES index_info (id)
    ON DELETE CASCADE
    );

-- 조회 성능 최적화 (최신 데이터 조회)
CREATE INDEX IF NOT EXISTS idx_index_data_lookup
    ON index_data (index_id, base_date DESC);



-- ================================
--  sync_info (동기화 기록)
-- ================================
CREATE TABLE IF NOT EXISTS sync_info (
                                         id          UUID PRIMARY KEY,
                                         index_id    UUID NOT NULL,
                                         job_type    VARCHAR(20),
    target_date DATE,
    worker      VARCHAR(20),
    job_time    TIMESTAMPTZ,
    result      VARCHAR(20),

    CONSTRAINT fk_syncinfo_index
    FOREIGN KEY (index_id)
    REFERENCES index_info (id)
    ON DELETE CASCADE
    );

-- 최근 성공 기록 조회 최적화
CREATE INDEX IF NOT EXISTS idx_sync_info_last_success
    ON sync_info (index_id, job_type, target_date DESC)
    WHERE result = '성공';