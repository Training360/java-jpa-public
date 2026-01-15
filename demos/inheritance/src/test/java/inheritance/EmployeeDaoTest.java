package inheritance;

import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EmployeeDaoTest {

    private EmployeeDao employeeDao;

    @Before
    public void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pu");
        employeeDao = new EmployeeDao(entityManagerFactory);
    }

    @Test
    public void testSaveAndFind() {
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
