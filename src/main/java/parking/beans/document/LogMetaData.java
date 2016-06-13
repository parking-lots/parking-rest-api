package parking.beans.document;

import java.util.ArrayList;

public class LogMetaData {
    ArrayList<String> fullName;
    boolean passwordChanged;
    ArrayList<String> email;
    String[][] cars;


    public ArrayList<String> getFullName() {
        return fullName;
    }

    public void setFullName(ArrayList<String> fullName) {
        this.fullName = fullName;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public ArrayList<String> getEmail() {
        return email;
    }

    public void setEmail(ArrayList<String> email) {
        this.email = email;
    }

    public String[][] getCars() {
        return cars;
    }

    public void setCars(String[][] cars) {
        this.cars = cars;
    }
}
