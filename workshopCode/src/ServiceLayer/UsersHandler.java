package ServiceLayer;

import DomainLayer.Security.SecurityHandler;
import DomainLayer.SystemHandler;
import DomainLayer.SystemLogger;

public class UsersHandler {

    public String login(String username, String password, boolean mood){

        SystemLogger.getInstance().writeEvent("Login command: " + username);

        if (!SecurityHandler.getInstance().CorrectPassword(username, password)) {
            SystemLogger.getInstance().writeError("Invalid password");
            return "This password is incorrect";
        }
        try {
            String[] args ={username, password};
            if(SystemHandler.getInstance().emptyString(args))
                throw new IllegalArgumentException("The username is invalid");
            if (! SystemHandler.getInstance().userExists(username))
                throw new IllegalArgumentException("This user is not registered");
            if(mood && !(SystemHandler.getInstance().checkIfUserIsAdmin(username)))
                throw new IllegalArgumentException("this user is not a system admin");
            SystemHandler.getInstance().login(username, mood);
            return "You have been successfully logged in!";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Login error: " + e.getMessage());
            return e.getMessage();
        }
    }


    public String logout(){
        SystemLogger.getInstance().writeEvent("Logout command");
        return SystemHandler.getInstance().logout();
    }


    public String register(String username, String password){
        SystemLogger.getInstance().writeEvent("Register command: " + username);
        if (!SecurityHandler.getInstance().validPassword(password)) {
            SystemLogger.getInstance().writeError("Invalid password");
            return "This password is not valid. Please choose a different one";
        }
        try {
            String[] args = {username};
            if (SystemHandler.getInstance().emptyString(args))
                throw new IllegalArgumentException("Username cannot be empty");
            if (SystemHandler.getInstance().userExists(username))
                throw new IllegalArgumentException("This username already exists in the system. Please choose a different one");
            SystemHandler.getInstance().register(username);
            SecurityHandler.getInstance().addUser(username, password);
            return "You have been successfully registered!";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Register error: " + e.getMessage());
            return e.getMessage();
        }
    }

    public void resetUsers(){
        SystemHandler.getInstance().resetUsers();
    }

    public void resetAdmins(){
        SystemHandler.getInstance().resetAdmins();

    }

    public String addAdmin(String username) {
        try {
            String[] args = {username};
            if (SystemHandler.getInstance().emptyString(args))
                throw new IllegalArgumentException("Username cannot be empty");
            if (!SystemHandler.getInstance().userExists(username))
                throw new IllegalArgumentException("This username not exist");
            if (SystemHandler.getInstance().checkIfUserIsAdmin(username))
                throw new IllegalArgumentException("This username is already admin");

            SystemHandler.getInstance().addAdmin(username);
            return"done";
        }
        catch (Exception e){
            return e.getMessage();
        }
    }


}

