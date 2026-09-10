INSERT INTO tb_roles (role_id, name) VALUES (1, 'ADMIN') ON CONFLICT (role_id) DO NOTHING;
INSERT INTO tb_roles (role_id, name) VALUES (2, 'BASIC') ON CONFLICT (role_id) DO NOTHING;


SELECT setval(pg_get_serial_sequence('tb_roles', 'role_id'), COALESCE(MAX(role_id), 1)) FROM tb_roles;