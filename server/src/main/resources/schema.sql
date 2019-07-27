-- categories
CREATE TABLE  IF NOT EXISTS public.categories (
    id uuid NOT NULL,
    name character varying(64),
    CONSTRAINT categories_pkey PRIMARY KEY (id),
    CONSTRAINT uk_categories_name UNIQUE (name)
);

ALTER TABLE public.categories OWNER TO postgres;

-- albums
CREATE TABLE IF NOT EXISTS public.albums (
    id uuid NOT NULL,
    name character varying(64),
    category_id uuid NOT NULL,
    CONSTRAINT albums_pkey PRIMARY KEY (id),
    CONSTRAINT fk_albums_category_id_categories_id FOREIGN KEY (category_id) REFERENCES public.categories(id) ON DELETE CASCADE
);

ALTER TABLE public.albums OWNER TO postgres;

-- animals
CREATE TABLE IF NOT EXISTS public.animals (
    id uuid NOT NULL,
    name character varying(32),
    tags bytea,
    category_id uuid NOT NULL,
    CONSTRAINT animals_pkey PRIMARY KEY (id),
    CONSTRAINT fk_animals_category_id_categories_id FOREIGN KEY (category_id) REFERENCES public.categories(id) ON DELETE CASCADE
);

ALTER TABLE public.animals OWNER TO postgres;

-- photos
CREATE TABLE IF NOT EXISTS public.photos (
    id uuid NOT NULL,
    content bytea,
    created_at timestamp with time zone,
    last_modified timestamp without time zone,
    description character varying(255),
    address character varying(255),
    latitude double precision,
    longitude double precision,
    CONSTRAINT photos_pkey PRIMARY KEY (id)
);

ALTER TABLE public.photos OWNER TO postgres;

-- photo_animal
CREATE TABLE IF NOT EXISTS public.photo_animal (
    photo_id uuid NOT NULL,
    animal_id uuid NOT NULL,
    CONSTRAINT photo_animal_pkey PRIMARY KEY (photo_id, animal_id),
    CONSTRAINT fk_photo_animal_photo_id_photos_id FOREIGN KEY (photo_id) REFERENCES public.photos(id),
    CONSTRAINT fk_photo_animal_animal_id_animals_id FOREIGN KEY (animal_id) REFERENCES public.animals(id)
);

ALTER TABLE public.photo_animal OWNER TO postgres;

-- photo_album
CREATE TABLE IF NOT EXISTS public.photo_album (
    photo_id uuid NOT NULL,
    album_id uuid NOT NULL,
    CONSTRAINT photo_album_pkey PRIMARY KEY (photo_id, album_id),
    CONSTRAINT fk_photo_album_photo_id_photos_id FOREIGN KEY (photo_id) REFERENCES public.photos(id),
    CONSTRAINT fk_photo_album_album_id_albums_id FOREIGN KEY (album_id) REFERENCES public.albums(id)
);

ALTER TABLE public.photo_album OWNER TO postgres;

-- trigger
CREATE OR REPLACE FUNCTION delete_obsolete_photos() RETURNS trigger AS
'
    BEGIN
      DELETE FROM photos WHERE last_modified < NOW() - INTERVAL ''15 minute''
          AND (id NOT IN (SELECT photo_id FROM photo_animal))
          AND (id NOT IN (SELECT photo_id FROM photo_album));
      RETURN NULL;
    END
' LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_delete_obsolete_photos ON photos;

CREATE TRIGGER trigger_delete_obsolete_photos
    AFTER INSERT ON photos
    EXECUTE PROCEDURE delete_obsolete_photos();
