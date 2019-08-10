INSERT INTO categories (id, name)
VALUES
('8840a427-a863-4f0d-affc-7b5d22d36a0b', 'cat')
ON CONFLICT (id)
DO NOTHING;

INSERT INTO categories (id, name)
VALUES
('d8bca68e-f1f2-4769-afbc-2f58b8f63c87', 'dog')
ON CONFLICT (id)
DO NOTHING;