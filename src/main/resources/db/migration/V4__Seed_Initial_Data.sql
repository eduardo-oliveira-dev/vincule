-- Seed data para testes (Organização e Usuário Admin)

INSERT INTO organizations (id, name, description, address, created_at) 
VALUES ('00000000-0000-0000-0000-000000000001', 'ONG Esperança', 'Organização de apoio social', 'Rua Principal, 100', now()) 
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, organization_id, name, email, password_hash, role, created_at) 
VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Maria Silva', 'maria.silva@example.com', '$2a$10$z4Ewb61X5G0Oe20SrhIkVeyERnvGF61OCnO6XYDSQl55OfVVJQ0We', 'ADMIN', now()) 
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_impact (id, user_id, total_donations, total_hours, last_activity) 
VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002', 0, 0, now()) 
ON CONFLICT (user_id) DO NOTHING;
