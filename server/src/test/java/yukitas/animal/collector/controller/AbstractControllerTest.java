package yukitas.animal.collector.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.UUID;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup({ //
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {"classpath:schema.sql",
                "classpath" + ":before_test_script.sql"}), @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
        , scripts = "classpath:after_test_script.sql")})
public abstract class AbstractControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    static final UUID CATEGORY_CAT_ID = UUID.fromString("00000001-0000-0000-0000-000000000000");
    static final UUID CATEGORY_DOG_ID = UUID.fromString("00000001-0000-0000-0000-000000000001");
    static final String CATEGORY_CAT_NAME = "cat";
    static final String CATEGORY_DOG_NAME = "dog";
    static final String CATEGORY_HAMSTER_NAME = "hamster";

    static final UUID ALBUM_CAT_1_ID = UUID.fromString("00000000-0001-0000-0000-000000000000");
    static final UUID ALBUM_DOG_ID = UUID.fromString("00000000-0001-0000-0000-000000000001");
    static final String ALBUM_CAT_1_NAME = "album-cat-1";
    static final String ALBUM_CAT_2_NAME = "album-cat-2";
    static final String ALBUM_DOG_NAME = "album-dog";

    static final UUID ANIMAL_CAT_1_ID = UUID.fromString("00000000-0000-0001-0000-000000000000");
    static final UUID ANIMAL_DOG_ID = UUID.fromString("00000000-0000-0001-0000-000000000001");
    static final String ANIMAL_CAT_1_NAME = "animal-cat-1";
    static final String ANIMAL_CAT_2_NAME = "animal-cat-2";
    static final String ANIMAL_DOG_NAME = "animal-dog";
    static final String[] ANIMAL_CAT_2_TAGS = {"kawaii", "kakkoii", "sugoii"};

    static final UUID PHOTO_CAT_1_ID = UUID.fromString("00000000-0000-0000-0001-000000000000");
    static final UUID PHOTO_DOG_ID = UUID.fromString("00000000-0000-0000-0001-000000000001");
    static final byte[] PHOTO_CAT_1_CONTENT = "00000000".getBytes();
    static final byte[] PHOTO_DOG_CONTENT = "00000001".getBytes();
    static final String PHOTO_CAT_1_DESCRIPTION = "This photo contains animal-cat-1 and exists in album-cat-1";
    static final String PHOTO_DOG_DESCRIPTION = "This photo contains animal-dog and exists in album-dog";

    static final String LOCATION_ADDR = "Somewhere on the earth";

    TestRestTemplate getTestRestTemplate() {
        return testRestTemplate;
    }

    static <T> T getFixture(String fileName, Class<T> objectClass) throws Exception {
        return new ObjectMapper().readValue(new File("src/test/resources/fixtures/" + fileName), objectClass);
    }
}
