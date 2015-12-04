package parking.controllers;

import parking.beans.response.FormFieldError;
import parking.beans.response.FormFieldsErrorsWrapper;
import parking.exceptions.ExceedLoanLimitException;
import parking.exceptions.MonthlyPaymentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Iterator;
import java.util.List;

public class BaseController {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<FormFieldsErrorsWrapper> handleValidationException(MethodArgumentNotValidException e) {
        List<ObjectError> fieldSErrors = e.getBindingResult().getAllErrors();

        FormFieldsErrorsWrapper fieldErrorList = new FormFieldsErrorsWrapper();

        for (Iterator iterator = fieldSErrors.iterator(); iterator.hasNext(); ) {
            FieldError fieldError = (FieldError) iterator.next();
            fieldErrorList.addError(new FormFieldError(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        return new ResponseEntity<FormFieldsErrorsWrapper>(fieldErrorList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MonthlyPaymentException.class)
    @ResponseBody
    public ResponseEntity<FormFieldsErrorsWrapper> handleMonthlyPaymentException(MonthlyPaymentException e) {

        FormFieldsErrorsWrapper fieldErrorList = new FormFieldsErrorsWrapper();
        fieldErrorList.addError(new FormFieldError("salary", "The monthly payment must not exceed 40% of the salary."));

        return new ResponseEntity<FormFieldsErrorsWrapper>(fieldErrorList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExceedLoanLimitException.class)
    @ResponseBody
    public ResponseEntity<FormFieldsErrorsWrapper> handlleExceedLoanLimitException(ExceedLoanLimitException e) {

        FormFieldsErrorsWrapper fieldErrorList = new FormFieldsErrorsWrapper();
        fieldErrorList.addError(new FormFieldError("desiredSum", "Loan amount must not exceed total sum of 5 year salary income."));

        return new ResponseEntity<FormFieldsErrorsWrapper>(fieldErrorList, HttpStatus.BAD_REQUEST);
    }
}
