package yukitas.animal.collector.service.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Classify a detected animal into different categories (animal classes).
 *
 * Trained model source: https://dl.dropboxusercontent.com/s/tqnp49apphpzb40/dataTraining.zip?dl=0 Architecture: VGG-16
 * (For cats and dogs only)
 */
public class ImageRecognizer {
    private static final Logger LOGGER = LogManager.getLogger(ImageRecognizer.class);

    private static final String MODEL_FILE_PATH = "/model.zip";

    private ComputationGraph computationGraph;

    public AnimalClass classify(byte[] content, Double threshold) throws IOException {
        if (computationGraph == null) {
            computationGraph = loadModel();
        }

        computationGraph.init();

        LOGGER.info(computationGraph.summary());

        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
        INDArray image = loader.asMatrix(new ByteArrayInputStream(content));
        DataNormalization scaler = new VGG16ImagePreProcessor();
        scaler.transform(image);
        INDArray output = computationGraph.outputSingle(false, image);
        if (output.getDouble(0) > threshold) {
            return AnimalClass.CAT;
        } else if (output.getDouble(1) > threshold) {
            return AnimalClass.DOG;
        } else {
            return AnimalClass.UNKNOWN;
        }
    }

    private ComputationGraph loadModel() throws IOException {
        return ModelSerializer.restoreComputationGraph(new File(getClass().getResource(MODEL_FILE_PATH).getFile()));
    }
}
