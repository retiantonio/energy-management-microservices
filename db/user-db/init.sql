CREATE TABLE users (
    uuid         UUID PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL UNIQUE,
    address      VARCHAR(255) NOT NULL
);

CREATE TABLE client (
    uuid UUID PRIMARY KEY REFERENCES users(uuid) ON DELETE CASCADE
);

CREATE TABLE administrator (
    uuid UUID PRIMARY KEY REFERENCES users(uuid) ON DELETE CASCADE
);

INSERT INTO users (uuid, name, email, address)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'Admin',  'admin@example.com',  'HQ Street 1'),
  ('22222222-2222-2222-2222-222222222222', 'Client', 'client@example.com', 'Client Street 2')
ON CONFLICT (uuid) DO NOTHING;

INSERT INTO administrator (uuid) VALUES ('11111111-1111-1111-1111-111111111111')
ON CONFLICT (uuid) DO NOTHING;

INSERT INTO client (uuid) VALUES ('22222222-2222-2222-2222-222222222222')
ON CONFLICT (uuid) DO NOTHING;