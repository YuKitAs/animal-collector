package yukitas.animal.collector.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class FileReader {
    public static <T> T getFixture(String filePath, Class<T> objectClass) throws Exception {
        return new ObjectMapper().readValue(new File("src/test/resources/fixtures/" + filePath), objectClass);
    }
}
