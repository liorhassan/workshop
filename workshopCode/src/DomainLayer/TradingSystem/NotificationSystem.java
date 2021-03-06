package DomainLayer.TradingSystem;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NotificationSystem {

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> notification;
    private ConcurrentHashMap<String, Boolean> loggedIn;
    private static NotificationSystem ourInstance = new NotificationSystem();
    public static NotificationSystem getInstance() {
        return ourInstance;
    }


    public NotificationSystem(){
        this.notification = new ConcurrentHashMap<>();
        this.loggedIn = new ConcurrentHashMap<>();
    }

    public void addUser(String userName) {
        notification.put(userName, new ConcurrentLinkedQueue<>());
        loggedIn.put(userName,false);
    }
    public void logInUser(String username) {
        loggedIn.put(username,true);
        if(notification.get(username).size()>0)
            synchronized (this) {notify();}
    }
    public void logOutUser(String username) {
        loggedIn.put(username,false);
        synchronized (this) {notify();}
    }

    public void notify(String userName, String msg){
        if(notification.containsKey(userName)) {
            notification.get(userName).add(msg);
            if(loggedIn.get(userName))
                synchronized (this) {notify();}
        }
    }

    public int getUserNotificationNumber(String userName){
        if(notification.contains(userName)){
            return notification.get(userName).size();
        }
        return 0;
    }

    public JSONObject getNotifications(){
        while(true) {
            for (String username : loggedIn.keySet()) {
                if (loggedIn.get(username) && notification.get(username).size()>0) {
                    JSONObject output = new JSONObject();
                    output.put("username",username);
                    JSONArray noti = new JSONArray();
                    while(notification.get(username).size()>0)
                        noti.add(notification.get(username).poll());
                    output.put("notifications",noti);
                    return output;
                }
            }
            try {synchronized (this) {wait();}}
            catch (InterruptedException e) {e.printStackTrace();}
        }
    }


}
