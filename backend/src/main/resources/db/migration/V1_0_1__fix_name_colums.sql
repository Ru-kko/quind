ALTER TABLE client
ADD CONSTRAINT check_birth_date CHECK (birth_date < CURRENT_DATE);

ALTER TABLE account
RENAME COLUMN accountType TO account_type;

ALTER TABLE account
RENAME COLUMN accountStatus TO account_status;

ALTER TABLE transaction
RENAME COLUMN transactionType TO transaction_type;