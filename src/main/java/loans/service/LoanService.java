package loans.service;

import loans.beans.request.Customer;
import loans.beans.response.Loan;
import loans.exceptions.ExceedLoanLimitException;
import loans.exceptions.MonthlyPaymentException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class LoanService {

    private static final BigDecimal INTEREST_PERCENT = new BigDecimal(0.05);
    private static final int YEARS_LIMIT = 5;
    private static final BigDecimal MAX_SALARY_PART = new BigDecimal(0.40);


    public Loan getLoan(Customer customer) throws MonthlyPaymentException, ExceedLoanLimitException {
        Loan loan = this.prepareLoan(customer);
        this.applyCondition(customer, loan);
        return this.prepareLoan(customer);
    }

    private void checkMonthlyPayment(Customer customer) {
        BigDecimal maxAmount = customer.getSalary().multiply(MAX_SALARY_PART);
    }

    private Loan prepareLoan(Customer customer) {
        BigDecimal interestRate = customer.getDesiredSum().multiply(INTEREST_PERCENT);
        BigDecimal totalLoanAmount = customer.getDesiredSum().add(interestRate);
        BigDecimal monthlyPayment = totalLoanAmount.divide(new BigDecimal(customer.getPeriodYears() * 12), 2, RoundingMode.HALF_UP);

        BigDecimal highestLoan = customer.getSalary()
                .multiply(new BigDecimal(YEARS_LIMIT))
                .multiply(new BigDecimal(1).subtract(INTEREST_PERCENT))
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        Loan loan = new Loan();
        loan.setTotalAmount(totalLoanAmount.setScale(2, RoundingMode.HALF_UP));
        loan.setMonthlyPayment(monthlyPayment);
        loan.setHighestLoan(highestLoan);

        return loan;
    }

    private void applyCondition(Customer customer, Loan loan) throws MonthlyPaymentException, ExceedLoanLimitException {
        BigDecimal maxSalaryPart = customer.getSalary().multiply(MAX_SALARY_PART).setScale(2, BigDecimal.ROUND_HALF_UP);

        if (loan.getMonthlyPayment().compareTo(maxSalaryPart) > 0) {
            throw new MonthlyPaymentException();
        }

        if (customer.getDesiredSum().compareTo(loan.getHighestLoan()) > 0 ) {
            throw new ExceedLoanLimitException();
        }


    }
}
