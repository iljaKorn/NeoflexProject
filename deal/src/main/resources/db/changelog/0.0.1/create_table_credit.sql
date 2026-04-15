CREATE TABLE credit (
    credit_id UUID PRIMARY KEY,
    amount NUMERIC(19, 2),
    term INTEGER,
    monthly_payment NUMERIC(19, 2),
    rate NUMERIC(19, 2),
    psk NUMERIC(19, 2),
    payment_schedule JSONB,
    insurance_enabled BOOLEAN,
    salary_client BOOLEAN,
    credit_status VARCHAR(255)
);