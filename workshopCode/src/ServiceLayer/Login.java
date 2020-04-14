package ServiceLayer;

import DomainLayer.Security.SecurityHandler;
import DomainLayer.SystemHandler;

public class Login {

    public String login(String username, String password, boolean mood){
        if (!SecurityHandler.getInstance().CorrectPassword(username, password))
            return "This password is incorrect";
        try {
            SystemHandler.getInstance().login(username,mood);
            return "You have been successfully logged in!";
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
}
