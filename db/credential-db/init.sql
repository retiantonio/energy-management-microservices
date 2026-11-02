CREATE TABLE credentials (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20) NOT NULL DEFAULT 'CLIENT' CHECK (role IN ('ADMIN', 'CLIENT')),

    user_id       UUID NOT NULL UNIQUE
);

INSERT INTO credentials (username, password_hash, role, user_id)
VALUES
  ('admin',  '$2a$12$Kd6e6wY9Rd6yccBhgXVWneqxcjeQt/b3FwmBZFNs2jC5.ShIJPx4K',  'ADMIN',  '11111111-1111-1111-1111-111111111111'),
  ('client', '$2a$12$a3utVn57XrH7AouDGACUw.A67hi7DD9aQV0b1EEYZ5CpTRLu7L/Pi', 'CLIENT', '22222222-2222-2222-2222-222222222222')
ON CONFLICT (username) DO NOTHING;