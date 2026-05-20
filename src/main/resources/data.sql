-- ================================
--  테스트용 데이터 (대시보드 확인용)
-- ================================

-- index_info 테스트 데이터
INSERT INTO index_info (
    id, index_name, index_classification,
    employed_items_count, base_point_in_time,
    base_index, source_type, favorite,
    created_at, updated_at
)
VALUES (
           gen_random_uuid(),
           '테스트지수',
           'TEST',
           10,
           CURRENT_DATE,
           1000,
           'USER',
           TRUE,
           now(),
           now()
       );


-- index_data 테스트 데이터
INSERT INTO index_data (
    id, index_id, base_date, source_type,
    market_price, closing_price, high_price, low_price,
    versus, fluctuation_rate,
    trade_quantity, trade_price, market_total_amount,
    created_at, updated_at
)
SELECT
    gen_random_uuid(),
    id,
    CURRENT_DATE,
    'USER',
    1000, 1000, 1100, 900,
    10, 1.5,
    100, 1000, 100000,
    now(), now()
FROM index_info
         LIMIT 1;