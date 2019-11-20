package yukitas.animal.collector.service;

import java.io.IOException;

import yukitas.animal.collector.model.AnimalClass;

public interface ImageRecognitionService {
    AnimalClass classify(byte[] content, Double threshold) throws IOException;
}
