package ServiceLayer;

import DomainLayer.Security.SecurityFacade;
import DomainLayer.TradingSystem.SystemFacade;
import DomainLayer.TradingSystem.SystemLogger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class UsersHandler {

    public String login(String username, String password, boolean mood){

        SystemLogger.getInstance().writeEvent("Login command: " + username);

        if (!SecurityFacade.getInstance().CorrectPassword(username, password)) {
            SystemLogger.getInstance().writeError("Invalid password");
            JSONObject response = new JSONObject();
            response.put("ERROR", "This password is incorrect");
            return response.toJSONString();
            //return "This password is incorrect";
        }
        try {
            String[] args ={username, password};
            if(SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("The username is invalid");
            if (! SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This user is not registered");
            if(mood && !(SystemFacade.getInstance().checkIfUserIsAdmin(username)))
                throw new IllegalArgumentException("this user is not a system admin");
            SystemFacade.getInstance().login(username, mood);
            JSONObject response = new JSONObject();
            response.put("SUCCESS", "You have been successfully logged in!");
            return response.toJSONString();

            //return "You have been successfully logged in!";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Login error: " + e.getMessage());
            JSONObject response = new JSONObject();
            response.put("ERROR", e.getMessage());
            return response.toJSONString();
            //return e.getMessage();
        }
    }


    public String logout(){
        SystemLogger.getInstance().writeEvent("Logout command");
        JSONObject response = new JSONObject();
        response.put("SUCCESS", SystemFacade.getInstance().logout());
        return response.toJSONString();
        //return SystemFacade.getInstance().logout();
    }


    public String register(String username, String password){
        SystemLogger.getInstance().writeEvent("Register command: " + username);
        if (!SecurityFacade.getInstance().validPassword(password)) {
            SystemLogger.getInstance().writeError("Invalid password");
            return "This password is not valid. Please choose a different one";
        }
        try {
            String[] args = {username};
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Username cannot be empty");
            if (SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This username already exists in the system. Please choose a different one");
            SystemFacade.getInstance().register(username);
            SecurityFacade.getInstance().addUser(username, password);
            JSONObject response = new JSONObject();
            response.put("SUCCESS", "You have been successfully registered!");
            return response.toJSONString();
            //return "You have been successfully registered!";
        }
        catch (Exception e){
            SystemLogger.getInstance().writeError("Register error: " + e.getMessage());
            JSONObject response = new JSONObject();
            response.put("ERROR", e.getMessage());
            return response.toJSONString();
            //return e.getMessage();
        }
    }

    public void resetUsers(){
        SystemFacade.getInstance().resetUsers();
    }

    public void resetAdmins(){
        SystemFacade.getInstance().resetAdmins();

    }

    public String addAdmin(String username) {
        try {
            String[] args = {username};
            if (SystemFacade.getInstance().emptyString(args))
                throw new IllegalArgumentException("Username cannot be empty");
            if (!SystemFacade.getInstance().userExists(username))
                throw new IllegalArgumentException("This username not exist");
            if (SystemFacade.getInstance().checkIfUserIsAdmin(username))
                throw new IllegalArgumentException("This username is already admin");

            SystemFacade.getInstance().addAdmin(username);
            return"done";
        }
        catch (Exception e){
            return e.getMessage();
        }
    }


}

