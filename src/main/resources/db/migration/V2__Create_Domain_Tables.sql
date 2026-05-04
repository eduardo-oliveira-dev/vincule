-- V2__Create_Domain_Tables.sql

-- Criação da tabela inventory_items
CREATE TABLE inventory_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit VARCHAR(50) NOT NULL,
    expiry_date DATE,
    minimum_stock INTEGER NOT NULL,
    category VARCHAR(100) NOT NULL,
    is_critical BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_inventory_items_org FOREIGN KEY (org_id) REFERENCES organizations(id)
);

-- Criação da tabela donations
CREATE TABLE donations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id UUID NOT NULL,
    donor_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    expiry_date DATE,
    donated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_donations_item FOREIGN KEY (item_id) REFERENCES inventory_items(id),
    CONSTRAINT fk_donations_donor FOREIGN KEY (donor_id) REFERENCES users(id)
);

-- Criação da tabela volunteer_events
CREATE TABLE volunteer_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    event_date TIMESTAMP NOT NULL,
    max_volunteers INTEGER NOT NULL,
    category VARCHAR(100) NOT NULL,
    created_by UUID NOT NULL,
    CONSTRAINT fk_volunteer_events_org FOREIGN KEY (org_id) REFERENCES organizations(id),
    CONSTRAINT fk_volunteer_events_creator FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Criação da tabela volunteer_schedules
CREATE TABLE volunteer_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    event_id UUID NOT NULL,
    confirmed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    attended BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_volunteer_schedules_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_volunteer_schedules_event FOREIGN KEY (event_id) REFERENCES volunteer_events(id)
);

-- Criação da tabela user_impact
CREATE TABLE user_impact (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL,
    total_donations INTEGER NOT NULL DEFAULT 0,
    total_hours INTEGER NOT NULL DEFAULT 0,
    last_activity TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_impact_user FOREIGN KEY (user_id) REFERENCES users(id)
);
