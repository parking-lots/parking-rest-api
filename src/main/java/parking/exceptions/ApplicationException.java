package parking.exceptions;

public class ApplicationException extends Exception {
    protected String errorCause;

    public ApplicationException(String errorCause) {
        this.errorCause = errorCause;
    }

    public String getErrorCause() {
        return errorCause;
    }
}
