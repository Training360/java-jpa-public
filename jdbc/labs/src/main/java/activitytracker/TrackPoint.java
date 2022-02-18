package activitytracker;

import java.time.LocalDate;
import java.util.Objects;

public class TrackPoint {

    private int id;
    private LocalDate time;
    private double lat;
    private double lon;

    public TrackPoint(LocalDate time, double lat, double lon) {
        this.time = time;
        this.lat = lat;
        this.lon = lon;
    }

    public TrackPoint(int id, LocalDate time, double lat, double lon) {
        this(time, lat, lon);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public LocalDate getTime() {
        return time;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrackPoint that = (TrackPoint) o;
        return Double.compare(that.lat, lat) == 0 && Double.compare(that.lon, lon) == 0 && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, lat, lon);
    }
}
