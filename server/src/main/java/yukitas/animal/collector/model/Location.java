package yukitas.animal.collector.model;

import javax.persistence.Embeddable;

@Embeddable
public class Location {
    private double latitude;
    private double longitude;
    private String address;

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return "Somewhere on the earth";
    }

    @Override
    public String toString() {
        return String.format("Location[lat=%.5f, long=%.5f, address='%s']", latitude, longitude, address);
    }
}
