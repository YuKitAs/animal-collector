package yukitas.animal.collector.service;

import yukitas.animal.collector.model.AnimalClass;

public class ImageRecognitionServiceStub implements ImageRecognitionService {
    @Override
    public AnimalClass classify(byte[] content, Double threshold) {
        return AnimalClass.DOG;
    }
}
