CREATE TABLE account(
    id bigserial PRIMARY KEY,
    amount integer
);

CREATE TABLE operation(
    id bigserial PRIMARY KEY,
    amount integer,
    account_id integer,
    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id) ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE OR REPLACE FUNCTION trigger_amount_process() RETURNS TRIGGER AS 
    $BODY$
        DECLARE 
            amount_number integer;
        BEGIN
            UPDATE account SET amount = amount + NEW.amount WHERE id = NEW.account_id RETURNING amount INTO amount_number;
            IF amount_number < 0 THEN
                PERFORM pg_notify('account_less_than_0', NEW.id::text);
            END IF;

            RETURN NEW;
        END
    $BODY$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_amount_process AFTER INSERT ON operation FOR EACH ROW EXECUTE PROCEDURE trigger_amount_process();
