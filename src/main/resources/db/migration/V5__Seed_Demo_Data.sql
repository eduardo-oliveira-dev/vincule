-- Seed data para testes de Demonstração (Ricardo Admin e Beatriz Voluntária)

-- Inserir os usuários
INSERT INTO users (id, organization_id, name, email, password_hash, role, created_at) 
VALUES ('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000001', 'Ricardo (Administrador)', 'ricardo@vincule.org', '$2a$10$z4Ewb61X5G0Oe20SrhIkVeyERnvGF61OCnO6XYDSQl55OfVVJQ0We', 'ADMIN', now()) 
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, organization_id, name, email, password_hash, role, created_at) 
VALUES ('00000000-0000-0000-0000-000000000011', '00000000-0000-0000-0000-000000000001', 'Beatriz (Voluntária)', 'beatriz@email.com', '$2a$10$z4Ewb61X5G0Oe20SrhIkVeyERnvGF61OCnO6XYDSQl55OfVVJQ0We', 'VOLUNTEER', now()) 
ON CONFLICT (id) DO NOTHING;

-- Inserir os perfis de impacto
INSERT INTO user_impact (id, user_id, total_donations, total_hours, last_activity) 
VALUES ('00000000-0000-0000-0000-000000000012', '00000000-0000-0000-0000-000000000010', 0, 0, now()) 
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_impact (id, user_id, total_donations, total_hours, last_activity) 
VALUES ('00000000-0000-0000-0000-000000000013', '00000000-0000-0000-0000-000000000011', 0, 0, now()) 
ON CONFLICT (user_id) DO NOTHING;
