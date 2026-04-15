create table client (
    client_id UUID PRIMARY KEY,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    middle_name VARCHAR(255),
    birth_date DATE,
    email VARCHAR(255),
    gender VARCHAR(255),
    marital_status VARCHAR(255),
    dependent_amount INTEGER,
    passport JSONB,
    employment JSONB,
    account_number VARCHAR(255)
);