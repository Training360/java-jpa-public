package activitytracker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ActivityDaoTest {

    EntityManagerFactory factory;

    ActivityDao activityDao;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("pu");
        activityDao = new ActivityDao(factory);
    }

    @AfterEach
    void close() {
        factory.close();
    }

    @Test
    void testSaveAndFind() {
        activityDao.saveActivity(new Activity(LocalDateTime.of(2022, 4, 11, 6, 0),
                "morning running"));

        activityDao.saveActivity(new SimpleActivity(LocalDateTime.of(2022, 3, 14, 17, 30),
                "basketball championship", "elementary school"));
        activityDao.saveActivity(new ActivityWithTrack(LocalDateTime.of(2022, 4, 9, 10, 15),
                "hiking near Budapest", 12.7, 10800));

        Activity activity = activityDao.findActivityByDescription("morning running");
        assertEquals(LocalDateTime.of(2022, 4, 11, 6, 0), activity.getStartTime());

        Activity simpleActivity = activityDao.findActivityByDescription("basketball championship");
        assertEquals(LocalDateTime.of(2022, 3, 14, 17, 30), simpleActivity.getStartTime());
        assertEquals("elementary school", ((SimpleActivity) simpleActivity).getPlace());

        Activity activityWithTrack = activityDao.findActivityByDescription("hiking near Budapest");
        assertEquals(LocalDateTime.of(2022, 4, 9, 10, 15), activityWithTrack.getStartTime());
        assertEquals(12.7, ((ActivityWithTrack) activityWithTrack).getDistance());
        assertEquals(10800, ((ActivityWithTrack) activityWithTrack).getDuration());
    }
}