package employees;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class ParkingPlaceDao {

    private EntityManagerFactory entityManagerFactory;

    public ParkingPlaceDao(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void saveParkingPlace(ParkingPlace parkingPlace) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(parkingPlace);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public ParkingPlace findParkingPlaceNumber(int number) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        ParkingPlace parkingPlace = entityManager.createQuery("select p from ParkingPlace p where p.number = :number",
                ParkingPlace.class)
                .setParameter("number", number)
                .getSingleResult();
        entityManager.close();
        return parkingPlace;
    }
}
