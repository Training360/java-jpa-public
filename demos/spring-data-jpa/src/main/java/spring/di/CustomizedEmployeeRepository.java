package spring.di;

import java.util.List;

public interface CustomizedEmployeeRepository {

    List<Employee> findByNameStartingWith(String namePrefix);
}
