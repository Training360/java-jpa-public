package activitytracker;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityDao {

    private DataSource dataSource;

    public ActivityDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveActivity(Activity activity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("insert into activities(start_time, description, activity_type) values (?, ?, ?);")) {
            setStatementParametersByActivity(activity, stmt);
            stmt.executeUpdate();
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot insert.", sqle);
        }
    }

    public void saveActivities(List<Activity> activities) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("insert into activities(start_time, description, activity_type) values (?, ?, ?);")) {
            for (Activity activity : activities) {
                setStatementParametersByActivity(activity, stmt);
                stmt.executeUpdate();
            }
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot insert values.", sqle);
        }
    }

    public Activity findActivityById(long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select * from activities where id = ?;")) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            return returnActivityIfFound(rs);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot query.", sqle);
        }
    }

    private Activity returnActivityIfFound(ResultSet rs) throws SQLException {
        if (rs.next()) {
            int foundId = rs.getInt("id");
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            String description = rs.getString("description");
            ActivityType type = ActivityType.valueOf(rs.getString("activity_type"));

            return new Activity(foundId, startTime, description, type);
        }
        throw new ActivityNotFoundException("Activity with this id not found.");
    }

    public List<Activity> listActivities() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from activities;")) {

            return getActivities(rs);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot query.", sqle);
        }
    }

    private List<Activity> getActivities(ResultSet rs) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            String description = rs.getString("description");
            ActivityType type = ActivityType.valueOf(rs.getString("activity_type"));
            activities.add(new Activity(id, startTime, description, type));
        }

        return activities;
    }

    public Activity saveActivityAndReturnGeneratedKeys(Activity activity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("insert into activities(start_time, description, activity_type) values (?, ?, ?);",
                     Statement.RETURN_GENERATED_KEYS)) {
            setStatementParametersByActivity(activity, stmt);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return new Activity(rs.getInt(1), activity.getStartTime(), activity.getDescription(), activity.getType());
            }
            throw new IllegalStateException("Cannot get generated keys.");
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot insert.", sqle);
        }
    }

    public void saveActivityAndSaveTrackPoints(Activity activity) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int activityId = saveActivityWithTrackPoints(activity, conn);
                saveTrackPoints(activityId, activity, conn);
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw new IllegalArgumentException("Transaction not succeeded!", ex);
            }
        } catch (SQLException sqle) {
            throw new IllegalStateException("Wrong connection.", sqle);
        }
    }

    private int saveActivityWithTrackPoints(Activity activity, Connection conn) throws SQLException {
        int activityId = 0;
        try (PreparedStatement stmt = conn.prepareStatement("insert into activities(start_time, description, activity_type) values (?, ?, ?);",
                Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(activity.getStartTime()));
            stmt.setString(2, activity.getDescription());
            stmt.setString(3, activity.getType().toString());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                activityId = rs.getInt(1);
            }
            return activityId;
        }
    }

    private void saveTrackPoints(int activityId, Activity activity, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("insert into track_point(id, tp_time, lat, lon) values (?, ?, ?, ?);")) {
            for (TrackPoint t : activity.getTrackpoints()) {
                if (t.getLat() < -90.0 || t.getLat() > 90.0 || t.getLon() < -180 || t.getLon() > 180.0) {
                    throw new IllegalArgumentException("Not suitable values for latitude or longitude!");
                }
                stmt.setInt(1, activityId);
                stmt.setDate(2, Date.valueOf(t.getTime()));
                stmt.setDouble(3, t.getLat());
                stmt.setDouble(4, t.getLon());

                stmt.executeUpdate();
            }
        }
    }

    public Activity findActivityWithTrackPointsById(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select * from activities where id = ?;")) {
            stmt.setInt(1, id);
            return getActivityById(id, stmt, conn);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot select activity or trackpoints.", sqle);
        }
    }

    private Activity getActivityById(int id, PreparedStatement stmt, Connection conn) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                String description = rs.getString("description");
                ActivityType type = ActivityType.valueOf(rs.getString("activity_type"));
                List<TrackPoint> trackPoints = getTrackPoints(id, conn);
                return new Activity(id, startTime, description, type, trackPoints);
            }
            throw new IllegalArgumentException("No activity with this id.");
        }
    }

    private List<TrackPoint> getTrackPoints(int id, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement("select * from track_point where id = ?;")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return loadTrackPoints(rs);
            }
        }
    }

    private List<TrackPoint> loadTrackPoints(ResultSet rs) throws SQLException {
        List<TrackPoint> trackpoints = new ArrayList<>();
        while (rs.next()) {
            trackpoints.add(extractTrackPoint(rs));
        }
        return trackpoints;
    }

    public List<Activity> activitiesBeforeDate(LocalDate date) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from activities;")) {

            return getActivitiesBeforeDate(rs, date);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot query.", sqle);
        }
    }

    private List<Activity> getActivitiesBeforeDate(ResultSet rs, LocalDate date) throws SQLException {
        List<Activity> activitiesBeforeDate = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            String description = rs.getString("description");
            ActivityType type = ActivityType.valueOf(rs.getString("activity_type"));
            if (startTime.toLocalDate().isBefore(date)) {
                activitiesBeforeDate.add(new Activity(id, startTime, description, type));
            }
        }

        return activitiesBeforeDate;
    }

    public List<Activity> listActivitiesBeforeDate(LocalDate date) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select * from activities where start_time < ?;")) {

            LocalDateTime dateTime = date.atTime(0, 0);
            stmt.setTimestamp(1, Timestamp.valueOf(dateTime));
            return listActivitiesBeforeDate(stmt);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot query.", sqle);
        }
    }

    private List<Activity> listActivitiesBeforeDate(PreparedStatement stmt) throws SQLException {
        List<Activity> activitiesBeforeDate = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                String description = rs.getString("description");
                ActivityType type = ActivityType.valueOf(rs.getString("activity_type"));
                activitiesBeforeDate.add(new Activity(id, startTime, description, type));
            }
        }
        return activitiesBeforeDate;
    }

    public void saveImageToActivity(long activityId, Image image) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("insert into images(id, filename, content) values (?, ?, ?);")) {
            Blob blob = conn.createBlob();
            blob.setBytes(1, image.getContent());
            stmt.setLong(1, activityId);
            stmt.setString(2, image.getFilename());
            stmt.setBlob(3, blob);
            stmt.executeUpdate();
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot save image into table.", sqle);
        }
    }

    public Image loadImageToActivity(long activityId, String filename) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select * from images where id = ? and filename = ?;")) {
            stmt.setLong(1, activityId);
            stmt.setString(2, filename);
            return getImage(stmt, activityId, filename);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot load image.", sqle);
        }
    }

    private Image getImage(PreparedStatement stmt, long activityId, String filename) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                byte[] content = rs.getBytes("content");
                return new Image(activityId, filename, content);
            }
            throw new IllegalArgumentException("No image with this id.");
        }
    }

    public List<TrackPoint> getSomeTrackPoints(long activityId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select * from track_point where id = ?",
                     ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            stmt.setLong(1, activityId);
            return getFirstMediumLastTrackPoints(stmt);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot list trackpoints.", sqle);
        }
    }

    private List<TrackPoint> getFirstMediumLastTrackPoints(PreparedStatement stmt) throws SQLException {
        List<TrackPoint> firstMediumLastTrackPoints = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            rs.first();
            firstMediumLastTrackPoints.add(extractTrackPoint(rs));
            rs.last();
            firstMediumLastTrackPoints.add(extractTrackPoint(rs));
            int size = rs.getRow();
            rs.relative(- size / 2);
            firstMediumLastTrackPoints.add(extractTrackPoint(rs));
        }
        return firstMediumLastTrackPoints;
    }

    private TrackPoint extractTrackPoint(ResultSet rs) throws SQLException {
        LocalDate time = rs.getDate("tp_time").toLocalDate();
        double lat = rs.getDouble("lat");
        double lon = rs.getDouble("lon");

        return new TrackPoint(time, lat, lon);
    }

    private void setStatementParametersByActivity(Activity activity, PreparedStatement stmt) throws SQLException {
        stmt.setTimestamp(1, Timestamp.valueOf(activity.getStartTime()));
        stmt.setString(2, activity.getDescription());
        stmt.setString(3, activity.getType().toString());
    }
}