package employees;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

//    @ManyToMany
//    private Set<Employee> employees = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "project_employee",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
//    @MapKey(name = "cardNumber")
    @MapKeyColumn(name = "employee_in_project")
    private Map<String, Employee> employees = new HashMap<>();

    public Project() {
    }

    public Project(String name) {
        this.name = name;
    }

//    public void addEmployee(Employee employee) {
//        employees.add(employee);
//        employee.getProjects().add(this);
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public Set<Employee> getEmployees() {
//        return employees;
//    }
//
//    public void setEmployees(Set<Employee> employees) {
//        this.employees = employees;
//    }


    public Map<String, Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<String, Employee> employees) {
        this.employees = employees;
    }
}
