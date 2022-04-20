package activitytracker;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@NamedQuery(name = "findTrackPointCoordinatesByDate", query = "select new activitytracker.Coordinate(t.latitude, t.longitude) from TrackPoint t where t.activity.startTime > :time order by t.time")
public class TrackPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate time;

    private double latitude;

    private double longitude;

    @ManyToOne
    private Activity activity;

    public TrackPoint() {
    }

    public TrackPoint(LocalDate time, double latitude, double longitude) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
