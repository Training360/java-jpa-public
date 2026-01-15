package com.training360.jpaspring;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public class LogEntryDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void save(LogEntry logEntry) {
        entityManager.persist(logEntry);
    }

    public List<LogEntry> list() {
        return entityManager
                .createQuery("select l from LogEntry l order by l.id", LogEntry.class).getResultList();
    }
}
