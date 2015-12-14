package parking.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import parking.beans.response.FormFieldError;
import parking.beans.response.FormFieldsErrorsWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import parking.exceptions.UserException;
import parking.handler.wrapper.ErrorMessage;

import java.util.Iterator;
import java.util.List;

@ControllerAdvice
public class ErrorHandler {
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

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorMessage> handleOccupiedEmailException(UserException e){
        return new ResponseEntity<ErrorMessage>(new ErrorMessage(e.getErrorCause()),HttpStatus.BAD_REQUEST);
    }
}
