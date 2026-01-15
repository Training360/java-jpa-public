package activitytracker;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class AreaDao {

    private EntityManagerFactory factory;

    public AreaDao(EntityManagerFactory factory) {
        this.factory = factory;
    }

    public void saveArea(Area area) {
        EntityManager manager = factory.createEntityManager();
        try {
            manager.getTransaction().begin();
            manager.persist(area);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public Area findAreaByName(String name) {
        EntityManager manager = factory.createEntityManager();
        try {
            Area area = manager.createQuery("select a from Area a join fetch a.activities where name = :name",
                    Area.class)
                    .setParameter("name", name)
                    .getSingleResult();
            return area;
        } finally {
            manager.close();
        }
    }

    public Area findAreaById(long id) {
        EntityManager manager = factory.createEntityManager();
        try {
            Area area = manager.createQuery("select a from Area a join fetch a.cities where a.id = :id",
                    Area.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return area;
        } finally {
            manager.close();
        }
    }
}
