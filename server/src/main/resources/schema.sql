-- categories
CREATE TABLE  IF NOT EXISTS  public.categories (
    id uuid NOT NULL,
    name character varying(64)
);

ALTER TABLE public.categories OWNER TO postgres;

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT uk_categories_name UNIQUE (name);

-- albums
CREATE TABLE IF NOT EXISTS public.albums (
    id uuid NOT NULL,
    name character varying(64),
    category_id uuid NOT NULL
);

ALTER TABLE public.albums OWNER TO postgres;

ALTER TABLE ONLY public.albums
    ADD CONSTRAINT albums_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.albums
    ADD CONSTRAINT fk_albums_category_id_categories_id FOREIGN KEY (category_id) REFERENCES public.categories(id) ON DELETE CASCADE;

-- animals
CREATE TABLE IF NOT EXISTS public.animals (
    id uuid NOT NULL,
    name character varying(32),
    tags bytea,
    category_id uuid NOT NULL
);

ALTER TABLE public.animals OWNER TO postgres;

ALTER TABLE ONLY public.animals
    ADD CONSTRAINT animals_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.animals
    ADD CONSTRAINT fk_animals_category_id_categories_id FOREIGN KEY (category_id) REFERENCES public.categories(id) ON DELETE CASCADE;

-- photos
CREATE TABLE IF NOT EXISTS public.photos (
    id uuid NOT NULL,
    content bytea,
    created_at timestamp without time zone,
    description character varying(255),
    address character varying(255),
    latitude double precision,
    longitude double precision
);

ALTER TABLE public.photos OWNER TO postgres;

ALTER TABLE ONLY public.photos
    ADD CONSTRAINT photos_pkey PRIMARY KEY (id);

-- photo_animal
CREATE TABLE IF NOT EXISTS public.photo_animal (
    photo_id uuid NOT NULL,
    animal_id uuid NOT NULL
);

ALTER TABLE public.photo_animal OWNER TO postgres;

ALTER TABLE ONLY public.photo_animal
    ADD CONSTRAINT photo_animal_pkey PRIMARY KEY (photo_id, animal_id);

ALTER TABLE ONLY public.photo_animal
    ADD CONSTRAINT fk_photo_animal_photo_id_photos_id FOREIGN KEY (photo_id) REFERENCES public.photos(id);

ALTER TABLE ONLY public.photo_animal
    ADD CONSTRAINT fk_photo_animal_animal_id_animals_id FOREIGN KEY (animal_id) REFERENCES public.animals(id);

-- photo_album
CREATE TABLE IF NOT EXISTS public.photo_album (
    photo_id uuid NOT NULL,
    album_id uuid NOT NULL
);

ALTER TABLE public.photo_album OWNER TO postgres;

ALTER TABLE ONLY public.photo_album
    ADD CONSTRAINT photo_album_pkey PRIMARY KEY (photo_id, album_id);

ALTER TABLE ONLY public.photo_album
    ADD CONSTRAINT fk_photo_album_photo_id_photos_id FOREIGN KEY (photo_id) REFERENCES public.photos(id);

ALTER TABLE ONLY public.photo_album
    ADD CONSTRAINT fk_photo_album_album_id_albums_id FOREIGN KEY (album_id) REFERENCES public.albums(id);

-- trigger
CREATE OR REPLACE FUNCTION delete_obsolete_photos() RETURNS trigger AS
'
    BEGIN
      DELETE FROM photos WHERE created_at < NOW() - INTERVAL ''15 minute''
          AND (id NOT IN (SELECT photo_id FROM photo_animal))
          AND (id NOT IN (SELECT photo_id FROM photo_album));
      RETURN NULL;
    END
' LANGUAGE plpgsql;

CREATE TRIGGER trigger_delete_obsolete_photos
    AFTER INSERT ON photos
    EXECUTE PROCEDURE delete_obsolete_photos();
