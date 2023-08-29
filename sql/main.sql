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
                    user_id INT,
                    bank_id INT,
                    CONSTRAINT fk_user
                        FOREIGN KEY(user_id) REFERENCES user_(id),
                    CONSTRAINT fk_bank
                        FOREIGN KEY(bank_id) REFERENCES bank(id)
);