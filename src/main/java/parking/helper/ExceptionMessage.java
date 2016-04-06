package parking.helper;

/**
 * Created by Lina on 05/04/16.
 */
public enum ExceptionMessage {
    USER_ALREADY_LOGGED("user_already_logged"),
    WRONG_CREDENTIALS("wrong_credentials"),
    PARKING_OWNED_BY_ANOTHER("parking_owned_by_another"),
    USER_NOT_FOUND ("user_not_found"),
    PARKING_ALREADY_EXISTS ("parking_already_exists"),
    PARKING_DID_NOT_EXIST ("parking_did_not_exist"),
    NOT_LOGGED("not_logged");

    private String msg;

    ExceptionMessage(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
