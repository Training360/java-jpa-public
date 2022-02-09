package spring.di;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Sql(scripts = "classpath:/clear.sql")
public class EmployeeDaoTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testSaveThenList() {
        employeeRepository.save(new Employee("John Doe"));

        List<String> names = StreamSupport.stream(employeeRepository.findAll().spliterator(), false)
                .map(Employee::getName).collect(Collectors.toList());

        assertEquals(List.of("John Doe"), names);
    }

    @Test
    public void testPageable() {
        for (int i = 100; i < 130; i++) {
            String name = "John Doe " + i;
            employeeRepository.save(new Employee(name));
        }

        Page<Employee> page = employeeRepository.findAll(PageRequest.of(2, 10, Sort.by("name")));

        assertEquals(30, page.getTotalElements());
        assertEquals(2, page.getNumber());
        assertEquals(10, page.getNumberOfElements());

        assertEquals("John Doe 120", page.getContent().get(0).getName());
        assertEquals("John Doe 129", page.getContent().get(9).getName());
    }

    @Test
    public void testFindByNameIgnoreCase() {
        employeeRepository.save(new Employee("John Doe"));
        employeeRepository.save(new Employee("Jack Doe"));

        List<Employee> employees = employeeRepository.findByNameIgnoreCase("john doe");

        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).getName());
    }

    @Test
    public void testFindByNameLength() {
        employeeRepository.save(new Employee("John Doe"));
        employeeRepository.save(new Employee("John John Doe"));

        List<Employee> employees = employeeRepository.findByNameLength(8);

        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).getName());
    }

    @Test
    public void testFindByNameStartingWith() {
        employeeRepository.save(new Employee("John Doe"));

        List<Employee> employees = employeeRepository.findByNameStartingWith("Jo");

        assertEquals(1, employees.size());
        assertEquals("John Doe", employees.get(0).getName());
    }
}
