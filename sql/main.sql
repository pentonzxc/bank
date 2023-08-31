CREATE TABLE IF NOT EXISTS bank(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS user_(
    id SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    birth_date TEXT
);

CREATE TABLE IF NOT EXISTS account(
                    id SERIAL PRIMARY KEY,
                    balance NUMERIC DEFAULT 0,
                    opening_date TIMESTAMP NOT NULL,
                    account_number TEXT UNIQUE NOT NULL,
                    currency VARCHAR(3) NOT NULL,
                    user_id INT NOT NULL,
                    bank_id INT NOT NULL,
                    CONSTRAINT fk_user
                        FOREIGN KEY(user_id) REFERENCES user_(id),
                    CONSTRAINT fk_bank
                        FOREIGN KEY(bank_id) REFERENCES bank(id)
);

CREATE TABLE IF NOT EXISTS transaction_(
                   id uuid PRIMARY KEY,
                   begin_date TIMESTAMP NOT NULL,
                   end_date TIMESTAMP NOT NULL,
                   origin_account_id INT NOT NULL,
                   target_account_id INT NOT NULL,
                   CONSTRAINT fk_origin_account
                        FOREIGN KEY(origin_account_id) REFERENCES account(id),
                    CONSTRAINT fk_target_account
                        FOREIGN KEY(target_account_id) REFERENCES account(id)

);