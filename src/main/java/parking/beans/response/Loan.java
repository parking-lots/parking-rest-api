package parking.beans.response;

import java.math.BigDecimal;

public class Loan extends Response {

    private BigDecimal monthlyPayment;
    private BigDecimal highestLoan;
    private BigDecimal totalAmount;

    public BigDecimal getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(BigDecimal monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public BigDecimal getHighestLoan() {
        return highestLoan;
    }

    public void setHighestLoan(BigDecimal highestLoan) {
        this.highestLoan = highestLoan;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
