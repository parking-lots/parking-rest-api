package parking.exceptions;


public class ParkingException extends Exception {
    private String errorCause;

    public String getErrorCause() {
        return errorCause;
    }

    public ParkingException(String errorCause){
        this.errorCause = errorCause;
    }
}
