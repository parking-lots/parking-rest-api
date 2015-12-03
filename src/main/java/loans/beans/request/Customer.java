package loans.beans.request;

import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.*;
import java.math.BigDecimal;

public class Customer {
    @NotEmpty(message = "First name is required")
    private String firstName;
    @NotEmpty(message = "Last name is required")
    private String lastName;
    @NotNull(message = "Salary may not be emtpy")
    @DecimalMin(value = "1.00", message = "The decimal value can not be less than 1.00 digit ")
    private BigDecimal salary;
    @NotNull(message = "Loan sum may not be emtpy")
    @DecimalMin(value = "1.00", message = "The decimal value can not be less than 1.00 digit ")
    private BigDecimal desiredSum;
    @NotNull(message = "Loan sum may not be emtpy")
    @Min(value = 1, message = "Min period 1 year")
    private int periodYears;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public BigDecimal getDesiredSum() {
        return desiredSum;
    }

    public void setDesiredSum(BigDecimal desiredSum) {
        this.desiredSum = desiredSum;
    }

    public int getPeriodYears() {
        return periodYears;
    }

    public void setPeriodYears(int periodYears) {
        this.periodYears = periodYears;
    }
}
