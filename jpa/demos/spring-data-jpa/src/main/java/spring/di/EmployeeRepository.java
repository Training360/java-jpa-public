package spring.di;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long>,
    CustomizedEmployeeRepository {

    List<Employee> findByNameIgnoreCase(String name);

    @Query("select e from Employee e where length(e.name) = :nameLength")
    List<Employee> findByNameLength(@Param("nameLength") int nameLength);
}
