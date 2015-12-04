package parking.beans.response;

import java.util.ArrayList;
import java.util.List;

public class FormFieldsErrorsWrapper extends Response {
    private List<FormFieldError> errors;

    public FormFieldsErrorsWrapper() {
        this.errors = new ArrayList<FormFieldError>();
    }

    public List<FormFieldError> getErrors () {
        return errors;
    }

    public void addError(FormFieldError error) {
        this.errors.add(error);
    }
}
