package parking.beans.document;

import java.util.Map;

public class LogMetaData {
    Map<String, String> fullName;
    boolean passwordChanged;
    Map<String, String> email;
    Map<String, String[]> cars;

    public Map<String, String> getFullName() {
        return fullName;
    }

    public void setFullName(Map<String, String> fullName) {
        this.fullName = fullName;
    }

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }

    public Map<String, String> getEmail() {
        return email;
    }

    public void setEmail(Map<String, String> email) {
        this.email = email;
    }

    public Map<String, String[]> getCars() {
        return cars;
    }

    public void setCars(Map<String, String[]> cars) {
        this.cars = cars;
    }

    //    public String[][] getCars() {
//        return cars;
//    }
//
//    public void setCars(String[][] cars) {
//        this.cars = cars;
//    }
}
