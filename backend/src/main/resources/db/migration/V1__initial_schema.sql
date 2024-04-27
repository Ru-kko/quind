CREATE TABLE client (
  client_id UUID PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL CHECK (LENGTH(first_name) >= 2),
  last_name VARCHAR(255) NOT NULL CHECK (LENGTH(last_name) >= 2),
  birth_date DATE,
  email VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP
);


CREATE TYPE ACCOUNT_TYPE AS ENUM ('CHECKING', 'SAVING');
CREATE TYPE ACCOUNT_STATUS AS ENUM ('ACTIVE', 'INACTIVE', 'CANCELED');

CREATE TABLE account (
  account_id BIGINT PRIMARY KEY,
  client_id UUID NOT NULL,
  balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
  accountType ACCOUNT_TYPE NOT NULL,
  accountStatus ACCOUNT_STATUS NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (client_id) REFERENCES client(client_id)
);

CREATE TYPE TRANSACTION_TYPE AS ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER');

CREATE TABLE transaction (
  id UUID PRIMARY KEY,
  target_account_id BIGINT,
  transfer_account_id BIGINT,
  amount DECIMAL(19, 2) NOT NULL,
  transactionType TRANSACTION_TYPE NOT NULL,
  FOREIGN KEY (target_account_id) REFERENCES account(account_id),
  FOREIGN KEY (transfer_account_id) REFERENCES account(account_id)
);

CREATE SEQUENCE account_id_seq START WITH 1;

CREATE OR REPLACE FUNCTION generate_account_id()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.type = 0 THEN
        NEW.account_id := nextval('account_id_seq');
    ELSIF NEW.type = 1 THEN
        NEW.account_id := 5300000000 + nextval('account_id_seq');
    ELSE
        NEW.account_id := 3300000000 + nextval('account_id_seq');
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER account_before_insert_trigger
BEFORE INSERT ON account
FOR EACH ROW
EXECUTE FUNCTION generate_account_id();