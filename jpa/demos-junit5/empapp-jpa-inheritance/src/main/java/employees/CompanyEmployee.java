package employees;

import javax.persistence.Entity;

@Entity
public class CompanyEmployee extends Employee {

    private int vacation;

    public CompanyEmployee() {
    }

    public CompanyEmployee(String name, int vacation) {
        super(name);
        this.vacation = vacation;
    }

    public int getVacation() {
        return vacation;
    }

    public void setVacation(int vacation) {
        this.vacation = vacation;
    }
}
