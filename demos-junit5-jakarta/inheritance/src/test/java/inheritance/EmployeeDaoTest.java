package inheritance;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeDaoTest {

    EmployeeDao employeeDao;

    @BeforeEach
    void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pu");
        employeeDao = new EmployeeDao(entityManagerFactory);
    }

    @Test
    void testSaveAndFind() {
        employeeDao.saveEmployee(new Employee("John Doe"));

        employeeDao.saveEmployee(new CompanyEmployee("Jack Doe", 22));
        employeeDao.saveEmployee(new ContractEmployee("Jane Doe", 100_000));

        Employee employee = employeeDao.findEmployeeByName("John Doe");
        assertEquals("John Doe", employee.getName());

        Employee company = employeeDao.findEmployeeByName("Jack Doe");
        assertEquals("Jack Doe", company.getName());
        assertEquals(22, ((CompanyEmployee) company).getVacation());

        Employee contracted = employeeDao.findEmployeeByName("Jane Doe");
        assertEquals("Jane Doe", contracted.getName());
        assertEquals(100_000, ((ContractEmployee) contracted).getDailyRate());
    }
}
