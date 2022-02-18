package activitytracker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Activity {

    private int id;

    private LocalDateTime startTime;

    private String description;

    private ActivityType type;

    private List<TrackPoint> trackpoints;

    public Activity(LocalDateTime startTime, String description, ActivityType type) {
        this.startTime = startTime;
        this.description = description;
        this.type = type;
    }

    public Activity(int id, LocalDateTime startTime, String description, ActivityType type) {
        this(startTime, description, type);
        this.id = id;
    }

    public Activity(LocalDateTime startTime, String description, ActivityType type, List<TrackPoint> trackpoints) {
        this(startTime, description, type);
        this.trackpoints = trackpoints;
    }

    public Activity(int id, LocalDateTime startTime, String description, ActivityType type, List<TrackPoint> trackpoints) {
        this(id, startTime, description, type);
        this.trackpoints = trackpoints;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getDescription() {
        return description;
    }

    public ActivityType getType() {
        return type;
    }

    public List<TrackPoint> getTrackpoints() {
        return new ArrayList<>(trackpoints);
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", trackpoints=" + trackpoints +
                '}';
    }
}
