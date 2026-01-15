package jpa;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {

    public enum EmployeeType {FULL_TIME, HALF_TIME}

    @Id
    private EmployeeId id;

    @Column(name = "emp_name",length = 200, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType = EmployeeType.FULL_TIME;

    private LocalDate dateOfBirth;

    public Employee() {
    }

    public Employee(String depName, Long id, String name) {
        this.id = new EmployeeId(depName, id);
        this.name = name;
        this.dateOfBirth = LocalDate.of(2000, 1, 1);
    }

    public EmployeeId getId() {
        return id;
    }

    public void setId(EmployeeId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + id +
                ", name='" + name + '\'' +
                ", employeeType=" + employeeType +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
