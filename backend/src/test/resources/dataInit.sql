INSERT INTO client (client_id, first_name, last_name, birth_date, email)
VALUES
    ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'John', 'Doe', '1990-05-15', 'john.doe@example.com'),
    ('17e62166-fc85-86df-a4d1-bc0e1742c08b', 'Jane', 'Smith', '1985-10-20', 'jane.smith@example.com'),
    ('1f5c4e23-5bc3-4cd6-a0df-2b765e5d008a', 'Michael', 'Johnson', '1978-03-28', 'michael.johnson@example.com'),
    ('a7f4a41b-5c87-4d72-9672-0e82b3c1d467', 'Alice', 'Johnson', '1980-07-12', 'alice.johnson@example.com'),
    ('b8e5b32c-3c88-4d73-9473-1e93b3d2f468', 'Bob', 'Smith', '1975-06-24', 'bob.smith@example.com');

INSERT INTO account (account_id, client_id, balance, account_type, account_status)
VALUES
    (3300150001, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 1000.00, 'CHECKING', 'ACTIVE'),
    (3301003002, '17e62166-fc85-86df-a4d1-bc0e1742c08b', 2500.00, 'CHECKING', 'ACTIVE'),
    (3300000003, '1f5c4e23-5bc3-4cd6-a0df-2b765e5d008a', 500.00, 'CHECKING', 'ACTIVE'),
    (5300054001, 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 5000.00, 'SAVING', 'ACTIVE'),
    (5300454524, '17e62166-fc85-86df-a4d1-bc0e1742c08b', 7500.00, 'SAVING', 'ACTIVE'),
    (5300000003, '1f5c4e23-5bc3-4cd6-a0df-2b765e5d008a', 10000.00, 'SAVING', 'ACTIVE'),
    (5300032153, 'a7f4a41b-5c87-4d72-9672-0e82b3c1d467', 0.00, 'SAVING', 'ACTIVE'), 
    (5300054553, 'a7f4a41b-5c87-4d72-9672-0e82b3c1d467', 0.00, 'SAVING', 'CANCELED'),
    (5354654545, 'b8e5b32c-3c88-4d73-9473-1e93b3d2f468', 0.00, 'SAVING', 'CANCELED');

INSERT INTO transaction (id, target_account_id, transfer_account_id, amount, transaction_type)
VALUES
    ('2db51a93-6608-4dd1-b46a-5b4bfb44606a', 3300150001, NULL, 500.00, 'WITHDRAWAL'),
    ('72c3c5d5-23dc-4d2c-8711-cbfcf72f4550', 5300054001, NULL, 1000.00, 'WITHDRAWAL'),
    ('9c18461e-5cfb-4e56-805e-5f78d3f19473', NULL, 3301003002, 750.00, 'DEPOSIT'),
    ('14e92f6a-5aa4-4dd4-9e0c-11797e4b2aef', NULL, 5300454524, 1500.00, 'DEPOSIT');