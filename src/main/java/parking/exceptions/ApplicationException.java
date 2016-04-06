package parking.exceptions;

import parking.helper.ExceptionHandler;

public class ApplicationException extends Exception {
    protected String errorCause;

    public ApplicationException(String errorCause) {
        this.errorCause = errorCause;
    }

    public String getErrorCause() {
        return errorCause;
    }
}
