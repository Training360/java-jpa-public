package activitytracker;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ActivityDaoTest {

    ActivityDao dao;
    DatabaseMetadataDao dataDao;

    @BeforeEach
    void setUp() throws SQLException {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUrl("jdbc:mariadb://localhost:3306/activitytracker?useUnicode=true");
        dataSource.setUser("activitytracker");
        dataSource.setPassword("activitytracker");

        Flyway fw = Flyway.configure().dataSource(dataSource).load();
        fw.clean();
        fw.migrate();

        dao = new ActivityDao(dataSource);
        dataDao = new DatabaseMetadataDao(dataSource);

        Activity activity1 = new Activity(LocalDateTime.of(2021, 2, 22, 15, 35), "futás a parkban", ActivityType.RUNNING);
        dao.saveActivity(activity1);
        Activity activity2 = new Activity(LocalDateTime.of(2020, 8, 10, 11, 12), "Egész héten Zemplén!!!", ActivityType.HIKING);
        dao.saveActivity(activity2);

        List<Activity> activities = new ArrayList<>();
        Activity activity3 = new Activity(LocalDateTime.of(2020, 10, 2, 8, 15), "egész napos bicótúra", ActivityType.BIKING);
        activities.add(activity3);
        Activity activity4 = new Activity(LocalDateTime.of(2021, 1, 22, 9, 46), "meccs az iskola tornatermében", ActivityType.BASKETBALL);
        activities.add(activity4);
        Activity activity5 = new Activity(LocalDateTime.of(2020, 12, 22, 7, 52), "kis kör a tó körül", ActivityType.RUNNING);
        activities.add(activity5);
        dao.saveActivities(activities);
    }

    @Test
    void testFindActivityById() {
        Activity activity = dao.findActivityById(4);

        Assertions.assertEquals(LocalDateTime.of(2021, 1, 22, 9, 46), activity.getStartTime());
        Assertions.assertEquals("meccs az iskola tornatermében", activity.getDescription());
        Assertions.assertEquals(ActivityType.BASKETBALL, activity.getType());
    }

    @Test
    void testListActivities() {
        List<Activity> activities = dao.listActivities();

        Assertions.assertEquals(5, activities.size());
        Assertions.assertEquals(LocalDateTime.of(2020, 8, 10, 11, 12), activities.get(1).getStartTime());
        Assertions.assertEquals("egész napos bicótúra", activities.get(2).getDescription());
        Assertions.assertEquals(ActivityType.RUNNING, activities.get(4).getType());
    }

    @Test
    void testSaveActivityAndReturnGeneratedKeys() {
        Activity activity = new Activity(LocalDateTime.of(2021, 2, 23, 9, 56), "séta a kertben a napon", ActivityType.RUNNING);
        Activity expected = dao.saveActivityAndReturnGeneratedKeys(activity);

        Assertions.assertEquals(6, expected.getId());
    }

    @Test
    void testSaveActivitywithTrackPointsEverythingIsOK() {
        TrackPoint trackPoint1 = new TrackPoint(LocalDate.of(2021, 2, 24), 47.2181020, 18.5411940);
        TrackPoint trackPoint2 = new TrackPoint(LocalDate.of(2021, 2, 24), 47.2181230, 18.5411780);
        TrackPoint trackPoint3 = new TrackPoint(LocalDate.of(2020, 12, 14), 47.2302470, 18.5472280);
        TrackPoint trackPoint4 = new TrackPoint(LocalDate.of(2020, 12, 14), 47.2302550, 18.5472310);
        TrackPoint trackPoint5 = new TrackPoint(LocalDate.of(2020, 12, 14), 47.2302552, 18.5472312);
        List<TrackPoint> trackpoints = Arrays.asList(trackPoint1, trackPoint2, trackPoint3, trackPoint4, trackPoint5);
        Activity activity = new Activity(LocalDateTime.of(2020, 12, 14, 15, 30), "laza délutáni futás", ActivityType.RUNNING, trackpoints);

        dao.saveActivityAndSaveTrackPoints(activity);
        Activity expected = dao.findActivityWithTrackPointsById(6);

        Assertions.assertTrue(activity.getStartTime().equals(expected.getStartTime()));
        Assertions.assertTrue(activity.getDescription().equals(expected.getDescription()));
        Assertions.assertTrue(activity.getType().equals(expected.getType()));
        Assertions.assertTrue(activity.getTrackpoints().size() == expected.getTrackpoints().size());
        Assertions.assertTrue(expected.getTrackpoints().equals(trackpoints));
    }

    @Test
    void testSaveActivitywithTrackPointsSomethingIsWrong() {
        TrackPoint trackPoint1 = new TrackPoint(LocalDate.of(2021, 2, 24), 47.2181020, 18.5411940);
        TrackPoint trackPoint2 = new TrackPoint(LocalDate.of(2021, 2, 24), 47.2181230, 18.5411780);
        TrackPoint trackPoint3 = new TrackPoint(LocalDate.of(2020, 12, 14), 47.2302470, 15238.5472280);
        TrackPoint trackPoint4 = new TrackPoint(LocalDate.of(2020, 12, 14), 47.2302550, 18.5472310);
        List<TrackPoint> trackpoints = Arrays.asList(trackPoint1, trackPoint2, trackPoint3, trackPoint4);
        Activity activity = new Activity(LocalDateTime.of(2020, 12, 14, 15, 30), "laza délutáni futás", ActivityType.RUNNING, trackpoints);

        Exception ex1 = Assertions.assertThrows(IllegalArgumentException.class, () -> dao.saveActivityAndSaveTrackPoints(activity));
        Assertions.assertEquals("Transaction not succeeded!", ex1.getMessage());

        Exception ex2 = Assertions.assertThrows(IllegalArgumentException.class, () -> dao.findActivityWithTrackPointsById(6));
        Assertions.assertEquals("No activity with this id.", ex2.getMessage());
    }

    @Test
    void testActivitiesBeforeDate() {
        List<Activity> expected = dao.activitiesBeforeDate(LocalDate.of(2021, 1, 1));

        Assertions.assertEquals(3, expected.size());
        Assertions.assertEquals(2, expected.get(0).getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 8, 10, 11, 12), expected.get(0).getStartTime());
        Assertions.assertEquals(3, expected.get(1).getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 10, 2, 8, 15), expected.get(1).getStartTime());
        Assertions.assertEquals(5, expected.get(2).getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 12, 22, 7, 52), expected.get(2).getStartTime());
    }

    @Test
    void testListActivitiesBeforeDate() {
        List<Activity> expected = dao.listActivitiesBeforeDate(LocalDate.of(2021, 1, 1));

        Assertions.assertEquals(3, expected.size());
        Assertions.assertEquals(2, expected.get(0).getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 8, 10, 11, 12), expected.get(0).getStartTime());
        Assertions.assertEquals(3, expected.get(1).getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 10, 2, 8, 15), expected.get(1).getStartTime());
        Assertions.assertEquals(5, expected.get(2).getId());
        Assertions.assertEquals(LocalDateTime.of(2020, 12, 22, 7, 52), expected.get(2).getStartTime());
    }

    @Test
    void testSaveAndLoadImage() throws IOException {
        InputStream is = ActivityDaoTest.class.getResourceAsStream("/kosarmeccs.jpg");
        byte[] content = is.readAllBytes();
        Image image = new Image("kosarmeccs.jpg", content);
        dao.saveImageToActivity(4, image);
        Image expected = dao.loadImageToActivity(4, "kosarmeccs.jpg");

        Assertions.assertTrue(expected.getContent().length > 46000);
    }

    @Test
    void testMetaData() {
        List<String> columnsForActivities = dataDao.getColumnsForTable("activities");

        Assertions.assertTrue(columnsForActivities.contains("id"));
        Assertions.assertTrue(columnsForActivities.contains("start_time"));
        Assertions.assertTrue(columnsForActivities.contains("description"));
        Assertions.assertTrue(columnsForActivities.contains("activity_type"));

        List<String> columnsForTrackPoint = dataDao.getColumnsForTable("track_point");

        Assertions.assertTrue(columnsForTrackPoint.contains("id"));
        Assertions.assertTrue(columnsForTrackPoint.contains("tp_time"));
        Assertions.assertTrue(columnsForTrackPoint.contains("lat"));
        Assertions.assertTrue(columnsForTrackPoint.contains("lon"));

        List<String> columnsForImages = dataDao.getColumnsForTable("images");

        Assertions.assertTrue(columnsForImages.contains("id"));
        Assertions.assertTrue(columnsForImages.contains("filename"));
        Assertions.assertTrue(columnsForImages.contains("content"));
    }

    @Test
    void testGetSomeTrackPoints() {
        TrackPoint trackPoint1 = new TrackPoint(LocalDate.of(2021, 2, 24), 47.2181020, 18.5411940);
        TrackPoint trackPoint2 = new TrackPoint(LocalDate.of(2021, 2, 24), 47.2181230, 18.5411780);
        TrackPoint trackPoint3 = new TrackPoint(LocalDate.of(2020, 12, 14), 47.2302470, 18.5472280);
        TrackPoint trackPoint4 = new TrackPoint(LocalDate.of(2020, 12, 14), 47.2302550, 18.5472310);
        TrackPoint trackPoint5 = new TrackPoint(LocalDate.of(2020, 12, 14), 47.2302552, 18.5472312);
        List<TrackPoint> trackpoints = Arrays.asList(trackPoint1, trackPoint2, trackPoint3, trackPoint4, trackPoint5);
        Activity activity = new Activity(LocalDateTime.of(2020, 12, 14, 15, 30), "laza délutáni futás", ActivityType.RUNNING, trackpoints);

        dao.saveActivityAndSaveTrackPoints(activity);

        TrackPoint expectedTrackPoint1 = new TrackPoint(LocalDate.of(2021, 2, 24), 47.2181020, 18.5411940);
        TrackPoint expectedTrackPoint2 = new TrackPoint(LocalDate.of(2020, 12, 14), 47.2302552, 18.5472312);
        TrackPoint expectedTrackPoint3 = new TrackPoint(LocalDate.of(2020, 12, 14), 47.2302470, 18.5472280);

        List<TrackPoint> expected = dao.getSomeTrackPoints(6);

        Assertions.assertEquals(3, expected.size());
        Assertions.assertEquals(expectedTrackPoint1, expected.get(0));
        Assertions.assertEquals(expectedTrackPoint2, expected.get(1));
        Assertions.assertEquals(expectedTrackPoint3, expected.get(2));
    }
}