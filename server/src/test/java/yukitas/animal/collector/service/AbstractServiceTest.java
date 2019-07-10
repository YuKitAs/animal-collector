package yukitas.animal.collector.service;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractServiceTest {
    static final UUID INVALID_ID = UUID.fromString("00000000-0000-0001-0000-000000000000");
    static final String DESCRIPTION = "something new";

    static final String CATEGORY_NOT_FOUND_MESSAGE = "CATEGORY not found by id=" + INVALID_ID;
    static final String ALBUM_NOT_FOUND_MESSAGE = "ALBUM not found by id=" + INVALID_ID;
    static final String ANIMAL_NOT_FOUND_MESSAGE = "ANIMAL not found by id=" + INVALID_ID;
    static final String PHOTO_NOT_FOUND_MESSAGE = "PHOTO not found by id=" + INVALID_ID;
    static final String ANIMAL_IDS_NOT_PROVIDED_MESSAGE = "Required but not provided: animal_ids";
}
