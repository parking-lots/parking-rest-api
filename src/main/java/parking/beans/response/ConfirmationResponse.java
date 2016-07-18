package parking.beans.response;

public class ConfirmationResponse extends Response {
    String message;

    public ConfirmationResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
