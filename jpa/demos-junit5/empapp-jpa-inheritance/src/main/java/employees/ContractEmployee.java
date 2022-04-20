package employees;

import javax.persistence.Entity;

@Entity
public class ContractEmployee extends Employee {

    private int dailyRate;

    public ContractEmployee() {
    }

    public ContractEmployee(String name, int dailyRate) {
        super(name);
        this.dailyRate = dailyRate;
    }

    public int getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(int dailyRate) {
        this.dailyRate = dailyRate;
    }
}
