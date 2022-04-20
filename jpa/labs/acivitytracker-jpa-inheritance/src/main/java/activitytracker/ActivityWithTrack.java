package activitytracker;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class ActivityWithTrack extends Activity {

    private double distance;

    private int duration;

    public ActivityWithTrack() {
    }

    public ActivityWithTrack(LocalDateTime startTime, String description, double distance, int duration) {
        super(startTime, description);
        this.distance = distance;
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
