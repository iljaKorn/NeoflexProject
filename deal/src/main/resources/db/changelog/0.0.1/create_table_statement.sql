CREATE TABLE statement (
    statement_id UUID PRIMARY KEY,
    client_id UUID,
    credit_id UUID,
    status VARCHAR(255),
    creation_date TIMESTAMP,
    applied_offer JSONB,
    sign_date TIMESTAMP,
    ses_code VARCHAR(255),
    status_history JSONB,

    FOREIGN KEY (client_id) REFERENCES client(client_id),
    FOREIGN KEY (credit_id) REFERENCES credit(credit_id)
);