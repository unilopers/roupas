-- Seed de usuário inicial gerado automaticamente pelo DataInitializer
INSERT INTO tb_user (user_id, name, email, phone, address, role, password)
SELECT RANDOM_UUID(), 'Administrador', 'admin@louisvittao.com',
       '(43) 99999-0000', 'Rua das Roupas, 100', 'ADMIN', '$2a$10$HaoJnWExoL6js2URAn96q.Pg5u3Iou/zT0FqH3o8WzgpAJeSqzAHa'
WHERE NOT EXISTS (SELECT 1 FROM tb_user WHERE email = 'admin@louisvittao.com');
