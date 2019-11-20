package yukitas.animal.collector.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import yukitas.animal.collector.AnimalCollectorApplication;
import yukitas.animal.collector.service.ImageRecognitionService;
import yukitas.animal.collector.service.ImageRecognitionServiceStub;

@Configuration
@Import(AnimalCollectorApplication.class)
public class ImageRecognitionServiceConfiguration {
    @Bean
    public ImageRecognitionService imageRecognitionService() {
        return new ImageRecognitionServiceStub();
    }
}
