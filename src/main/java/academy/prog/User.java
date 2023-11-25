package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class User {
    private String userName;
    private Status status;
    private Date registrationDate;
    private  Date lastAppearanceDate;

    public User(String userName, Status status) {
        this.userName = userName;
        this.status = status;
        this.registrationDate = new Date();
    }

    public User() {
    }

    public String toJSON() {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        return gson.toJson(this);
    }

    public String getUserName() {
        return userName;
    }

    public Status getStatus() {
        return status;
    }

    public Date getLastAppearanceDate() {
        return lastAppearanceDate;
    }
}
