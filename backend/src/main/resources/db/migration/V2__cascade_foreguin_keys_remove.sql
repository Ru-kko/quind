ALTER TABLE account
DROP CONSTRAINT account_client_id_fkey,
ADD CONSTRAINT account_client_id_fkey
FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE SET NULL;

ALTER TABLE transaction
DROP CONSTRAINT transaction_target_account_id_fkey,
ADD CONSTRAINT transaction_target_account_id_fkey
FOREIGN KEY (target_account_id) REFERENCES account(account_id) ON DELETE SET NULL;

ALTER TABLE transaction
DROP CONSTRAINT transaction_transfer_account_id_fkey,
ADD CONSTRAINT transaction_transfer_account_id_fkey
FOREIGN KEY (transfer_account_id) REFERENCES account(account_id) ON DELETE SET NULL;