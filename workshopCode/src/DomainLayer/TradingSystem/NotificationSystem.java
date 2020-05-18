package DomainLayer.TradingSystem;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class NotificationSystem {

    private HashMap<String, List<String>> notification;
    private static NotificationSystem ourInstance = new NotificationSystem();
    public static NotificationSystem getInstance() {
        return ourInstance;
    }


    public NotificationSystem(){
        this.notification = new HashMap<>();
    }

    public void attach(String userName) {
        if(!notification.containsKey(userName))
            notification.put(userName, new LinkedList<>());
    }

    public void dettach(String userName) {
        notification.remove(userName);
    }


    public void notify(String userName, String msg){
        if(notification.containsKey(userName)){
            notification.get(userName).add(msg);
        }
        else {
            notification.put(userName, new LinkedList<>());
            notification.get(userName).add(msg);
        }
    }

    public String getByUsername(String userName) {
        if(!notification.get(userName).isEmpty())
            return notification.get(userName).get(0);
        return null;
    }

    public boolean isLoggedIn(String userName) {
        return notification.containsKey(userName);
    }

//    public void notifySuspended(String userName) {
//        Client userSocket = webSockets.get(userName);
//        for(String msg: notification.get(userName)){
//            //TODO: SEND MSG VVIA SOCKET
//        }
//
//        notification.remove(userName);
//    }
}
