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
null,
'00000001-0000-0000-0000-000000000000')
ON CONFLICT (id)
DO NOTHING;

INSERT INTO animals (id, name, tags, category_id)
VALUES
('00000000-0000-0001-0000-000000000001',
'animal-dog',
null,
'00000001-0000-0000-0000-000000000001')
ON CONFLICT (id)
DO NOTHING;

INSERT INTO photos (id, content, created_at, created_at_offset, description, address, latitude, longitude)
VALUES
('00000000-0000-0000-0001-000000000000',
'00000000',
'2019-06-01T00:00:00+00:00',
10800,
'This photo contains animal-cat-1 and exists in album-cat-1',
'Somewhere on the earth',
0, 0)
ON CONFLICT (id)
DO NOTHING;

INSERT INTO photos (id, content, created_at, created_at_offset, description, address, latitude, longitude)
VALUES
('00000000-0000-0000-0001-000000000001',
'00000001',
'2019-06-01T00:00:00+00:00',
10800,
'This photo contains animal-dog and exists in album-dog',
'Somewhere on the earth',
-1, 1)
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
('00000000-0000-0000-0001-000000000001',
'00000000-0001-0000-0000-000000000001')
ON CONFLICT (photo_id, album_id)
DO NOTHING;

INSERT INTO photo_animal (photo_id, animal_id)
VALUES
('00000000-0000-0000-0001-000000000000',
'00000000-0000-0001-0000-000000000000')
ON CONFLICT (photo_id, animal_id)
DO NOTHING;

INSERT INTO photo_animal (photo_id, animal_id)
VALUES
('00000000-0000-0000-0001-000000000001',
'00000000-0000-0001-0000-000000000001')
ON CONFLICT (photo_id, animal_id)
DO NOTHING;