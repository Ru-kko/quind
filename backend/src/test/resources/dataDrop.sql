DELETE FROM transaction;

DELETE FROM account;

DELETE FROM client;

ALTER SEQUENCE account_id_seq
RESTART WITH 1;