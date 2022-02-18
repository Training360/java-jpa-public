package activitytracker;

import org.mariadb.jdbc.MariaDbDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActivityTrackerMain {

    public static void main(String[] args) {

        List<Activity> activities = new ArrayList<>();
        Activity activity1 = new Activity(LocalDateTime.of(2021, 2, 22, 15, 35), "futás a parkban", ActivityType.RUNNING);
        activities.add(activity1);
        Activity activity2 = new Activity(LocalDateTime.of(2020, 8, 10, 11, 12), "Egész héten Zemplén!!!", ActivityType.HIKING);
        activities.add(activity2);
        Activity activity3 = new Activity(LocalDateTime.of(2020, 10, 2, 8, 15), "egész napos bicótúra", ActivityType.BIKING);
        activities.add(activity3);
        Activity activity4 = new Activity(LocalDateTime.of(2021, 1, 22, 9, 46), "meccs az iskola tornatermében", ActivityType.BASKETBALL);
        activities.add(activity4);
        Activity activity5 = new Activity(LocalDateTime.of(2020, 12, 22, 7, 52), "kis kör a tó körül", ActivityType.RUNNING);
        activities.add(activity5);

        MariaDbDataSource dataSource;

        try {
            dataSource = new MariaDbDataSource();
            dataSource.setUrl("jdbc:mariadb://localhost:3306/activitytracker?useUnicode=true");
            dataSource.setUser("activitytracker");
            dataSource.setPassword("activitytracker");
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot connect to database.", sqle);
        }

        ActivityTrackerMain main = new ActivityTrackerMain();
        main.saveActivities(dataSource, activities);
        List<Activity> allActivities = main.listAllActivities(dataSource);
        for (Activity activity : allActivities) {
            main.findActivityById(dataSource, activity.getId());
        }
    }

    private void saveActivities(DataSource dataSource, List<Activity> activities) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("insert into activities(start_time, description, activity_type) values (?, ?, ?);")) {
            for (Activity activity : activities) {
                stmt.setTimestamp(1, Timestamp.valueOf(activity.getStartTime()));
                stmt.setString(2, activity.getDescription());
                stmt.setString(3, activity.getType().toString());
                stmt.executeUpdate();
            }
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot insert.", sqle);
        }
    }

    private Optional<Activity> findActivityById(DataSource dataSource, int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("select * from activities where id = ?;")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            return getResult(rs);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot query.", sqle);
        }
    }

    private Optional<Activity> getResult(ResultSet rs) throws SQLException {
        if (rs.next()) {
            int foundId = rs.getInt("id");
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            String description = rs.getString("description");
            ActivityType type = ActivityType.valueOf(rs.getString("activity_type"));
            Activity activity = new Activity(foundId, startTime, description, type);
            rs.close();
            return Optional.of(activity);
        } else {
            rs.close();
            return Optional.empty();
        }
    }

    private List<Activity> listAllActivities(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from activities;")) {

            return listActivities(rs);
        } catch (SQLException sqle) {
            throw new IllegalStateException("Cannot query.", sqle);
        }
    }

    private List<Activity> listActivities(ResultSet rs) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        while (rs.next()) {
            int foundId = rs.getInt("id");
            LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
            String description = rs.getString("description");
            ActivityType type = ActivityType.valueOf(rs.getString("activity_type"));
            activities.add(new Activity(foundId, startTime, description, type));
        }
        return activities;
    }
}