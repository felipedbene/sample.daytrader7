CREATE TABLE quote_price_history (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL,
    price NUMERIC(14,2) NOT NULL,
    recorded_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_qph_symbol_time ON quote_price_history(symbol, recorded_at);
