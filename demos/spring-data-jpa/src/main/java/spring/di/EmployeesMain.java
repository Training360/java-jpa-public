package spring.di;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.stream.StreamSupport;

public class EmployeesMain {

    public static void main(String[] args) {
        try (
                AnnotationConfigApplicationContext context =
                        new AnnotationConfigApplicationContext(AppConfig.class)
                ) {
            EmployeeRepository employeeRepository =
                    context.getBean(EmployeeRepository.class);
            employeeRepository.save(new Employee("Jack Doe"));

            StreamSupport.stream(employeeRepository.findAll().spliterator(), false)
                    .map(Employee::getName).forEach(System.out::println);
        }
    }
}
