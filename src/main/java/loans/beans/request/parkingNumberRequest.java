package loans.beans.request;

import javax.validation.constraints.NotNull;

public class parkingNumberRequest {
    @NotNull(message = "Must provide parking number, stupid!")
    private Integer number;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}