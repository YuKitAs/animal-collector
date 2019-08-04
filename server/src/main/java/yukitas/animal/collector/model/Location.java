package yukitas.animal.collector.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Embeddable;

@Embeddable
public class Location {
    private Double latitude;

    private Double longitude;

    private String address;

    private Location() {
    }

    @JsonCreator
    public Location(@JsonProperty("latitude") Double latitude, @JsonProperty("longitude") Double longitude,
            @JsonProperty("address") String address) {
        this.latitude = latitude;
        this.longitude = longitude;

        if (address != null && !address.isBlank()) {
            this.address = address;
        } else {
            if (latitude != null && longitude != null) {
                this.address = String.format("%s, %s", latitude, longitude);
            } else {
                this.address = "Somewhere on the earth";
            }
        }
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return String.format("Location (lat=%.5f, long=%.5f, address='%s')", latitude, longitude, address);
    }
}
