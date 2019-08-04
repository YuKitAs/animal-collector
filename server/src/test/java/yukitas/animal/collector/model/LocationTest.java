package yukitas.animal.collector.model;

import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationTest {
    private Location location;
    private final double latitude = -90 + 180 * new Random().nextDouble();
    private final double longitude = -180 + 360 * new Random().nextDouble();
    private final String address = "Universe";
    private static final String DEFAULT_ADDRESS = "Somewhere on the earth";

    @Test
    public void nonNullCoordinatesAndNonNullAddress_ReturnsGivenAddress() {
        location = new Location(latitude, longitude, address);
        assertThat(location.getAddress()).isEqualTo(address);
    }

    @Test
    public void nullCoordinatesAndNonNullAddress_ReturnsGivenAddress() {
        location = new Location(null, null, address);
        assertThat(location.getAddress()).isEqualTo(address);
    }

    @Test
    public void nonNullCoordinatesAndNullAddress_ReturnsAddressFromCoordinates() {
        location = new Location(latitude, longitude, null);
        assertThat(location.getAddress()).isEqualTo(String.format("%s, %s", latitude, longitude));
    }

    @Test
    public void nullCoordinatesAndNullAddress_ReturnsDefaultAddress() {
        location = new Location(null, null, null);
        assertThat(location.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }
}