CREATE OR REPLACE FUNCTION generate_account_id()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.account_id = NULL THEN
        IF NEW.account_type = 'CHECKING' THEN
            NEW.account_id := 3300000000 + nextval('account_id_seq');
        ELSIF NEW.account_type = 'SAVING' THEN
            NEW.account_id := 5300000000 + nextval('account_id_seq');
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;