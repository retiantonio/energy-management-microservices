CREATE TABLE devices (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                   VARCHAR(150) NOT NULL,
    description            TEXT,
    max_energy_consumption DOUBLE PRECISION NOT NULL CHECK (max_energy_consumption >= 0),

    -- link to users (one-to-many for now)
    owner_user_id          UUID
);