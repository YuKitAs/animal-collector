package yukitas.animal.collector.service;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractServiceTest {
    static final UUID INVALID_ID = UUID.randomUUID();

    static final String CATEGORY_NOT_FOUND_MESSAGE = "CATEGORY not found by id=" + INVALID_ID;
    static final String ALBUM_NOT_FOUND_MESSAGE = "ALBUM not found by id=" + INVALID_ID;
    static final String ANIMAL_NOT_FOUND_MESSAGE = "ANIMAL not found by id=" + INVALID_ID;
    static final String PHOTO_NOT_FOUND_MESSAGE = "PHOTO not found by id=" + INVALID_ID;

    static final UUID CATEGORY_CAT_ID = UUID.fromString("00000001-0000-0000-0000-000000000000");
    static final UUID CATEGORY_DOG_ID = UUID.fromString("00000001-0000-0000-0000-000000000001");
    static final String CATEGORY_CAT_NAME = "cat";
    static final String CATEGORY_DOG_NAME = "dog";

    static final UUID ALBUM_CAT_1_ID = UUID.fromString("00000000-0001-0000-0000-000000000000");
    static final UUID ALBUM_CAT_2_ID = UUID.fromString("00000000-0001-0000-0000-000000000001");
    static final UUID ALBUM_DOG_ID = UUID.fromString("00000000-0001-0000-0000-000000000002");
    static final String ALBUM_CAT_1_NAME = "album-cat-1";
    static final String ALBUM_CAT_2_NAME = "album-cat-2";
    static final String ALBUM_DOG_NAME = "album-dog";

    static final UUID ANIMAL_CAT_1_ID = UUID.fromString("00000000-0000-0001-0000-000000000000");
    static final UUID ANIMAL_CAT_2_ID = UUID.fromString("00000000-0000-0001-0000-000000000001");
    static final UUID ANIMAL_DOG_ID = UUID.fromString("00000000-0000-0001-0000-000000000002");
    static final String ANIMAL_CAT_1_NAME = "animal-cat-1";
    static final String ANIMAL_CAT_2_NAME = "animal-cat-2";
    static final String ANIMAL_DOG_NAME = "animal-dog";
    static final String[] ANIMAL_CAT_1_TAGS = {"kawaii", "kakkoii", "sugoii"};

    static final UUID PHOTO_CAT_1_ID = UUID.fromString("00000000-0000-0000-0001-000000000000");
    static final UUID PHOTO_CAT_12_ID = UUID.fromString("00000000-0000-0000-0001-000000000001");
    static final UUID PHOTO_DOG_ID = UUID.fromString("00000000-0000-0000-0001-000000000002");
}
