package activitytracker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AreaDaoTest {

    EntityManagerFactory factory;

    AreaDao areaDao;

    ActivityDao activityDao;

    @BeforeEach
    void init() {
        factory = Persistence.createEntityManagerFactory("pu");
        areaDao = new AreaDao(factory);
        activityDao = new ActivityDao(factory);
    }

    @AfterEach
    void close() {
        factory.close();
    }

   @Test
   void testSaveArea() {
       Activity firstActivity = new Activity(LocalDateTime.of(2022, 4, 11, 6, 0), "morning running", ActivityType.RUNNING);
       Activity secondActivity = new Activity(LocalDateTime.of(2022, 4, 9, 10, 15), "hiking near Budapest", ActivityType.HIKING);
       Activity thirdActivity = new Activity(LocalDateTime.of(2022, 3, 14, 17, 30), "evening running", ActivityType.RUNNING);
       activityDao.saveActivity(firstActivity);
       activityDao.saveActivity(secondActivity);
       activityDao.saveActivity(thirdActivity);

       Area firstArea = new Area("Kiskunság");
       Area secondArea = new Area("Hortobágy");
       Area thirdArea = new Area("Északi Középhegység");

       firstArea.addActivity(firstActivity);
       firstArea.addActivity(secondActivity);

       secondArea.addActivity(secondActivity);

       thirdArea.addActivity(firstActivity);
       thirdArea.addActivity(secondActivity);
       thirdArea.addActivity(thirdActivity);

       areaDao.saveArea(firstArea);
       areaDao.saveArea(secondArea);
       areaDao.saveArea(thirdArea);

       Area area = areaDao.findAreaByName("Kiskunság");

       assertEquals(Set.of("morning running", "hiking near Budapest"), area.getActivities().stream().map(Activity::getDescription).collect(Collectors.toSet()));
   }

   @Test
    public void testSaveThenFind() {
       Area area = new Area("Kiskunság");
       area.putCity(new City("Kecskemét", 110_687));
       area.putCity(new City("Soltvadkert", 7342));
       areaDao.saveArea(area);
       Area expected = areaDao.findAreaById(area.getId());

       assertEquals(2, expected.getCities().size());
       assertEquals(Set.of("Kecskemét", "Soltvadkert"), expected.getCities().keySet());
   }
}