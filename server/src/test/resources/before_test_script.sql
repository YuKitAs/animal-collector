INSERT INTO categories (id, name)
VALUES
('00000001-0000-0000-0000-000000000000', 'cat')
ON CONFLICT (id)
DO NOTHING;

INSERT INTO categories (id, name)
VALUES
('00000001-0000-0000-0000-000000000001', 'dog')
ON CONFLICT (id)
DO NOTHING;

INSERT INTO albums (id, name, category_id)
VALUES
('00000000-0001-0000-0000-000000000000',
'album-cat-1',
'00000001-0000-0000-0000-000000000000')
ON CONFLICT (id)
DO NOTHING;

INSERT INTO albums (id, name, category_id)
VALUES
('00000000-0001-0000-0000-000000000001',
'album-dog',
'00000001-0000-0000-0000-000000000001')
ON CONFLICT (id)
DO NOTHING;

INSERT INTO animals (id, name, tags, category_id)
VALUES
('00000000-0000-0001-0000-000000000000',
'animal-cat-1',
'{}',
'00000001-0000-0000-0000-000000000000')
ON CONFLICT (id)
DO NOTHING;

INSERT INTO animals (id, name, tags, category_id)
VALUES
('00000000-0000-0001-0000-000000000002',
'animal-dog',
'{}',
'00000001-0000-0000-0000-000000000001')
ON CONFLICT (id)
DO NOTHING;

INSERT INTO photos (id, content, created_at, description, address, latitude, longitude)
VALUES
('00000000-0000-0000-0001-000000000000',
'00000000',
'2019-01-01T00:00:00.123456Z',
'This photo contains animal-1 and exists in album-1 and album-2',
'Somewhere on the earth',
0, 0)
ON CONFLICT (id)
DO NOTHING;

INSERT INTO photo_album (photo_id, album_id)
VALUES
('00000000-0000-0000-0001-000000000000',
'00000000-0001-0000-0000-000000000000')
ON CONFLICT (photo_id, album_id)
DO NOTHING;

INSERT INTO photo_album (photo_id, album_id)
VALUES
('00000000-0000-0000-0001-000000000000',
'00000000-0001-0000-0000-000000000001')
ON CONFLICT (photo_id, album_id)
DO NOTHING;

INSERT INTO photo_animal (photo_id, animal_id)
VALUES
('00000000-0000-0000-0001-000000000000',
'00000000-0000-0001-0000-000000000000')
ON CONFLICT (photo_id, animal_id)
DO NOTHING;