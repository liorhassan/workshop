package DomainLayer.TradingSystem;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class NotificationSystem {

    private HashMap<String, List<String>> suspendedNotification;
    private static NotificationSystem ourInstance = new NotificationSystem();
    public static NotificationSystem getInstance() {
        return ourInstance;
    }


    public NotificationSystem(){
        this.suspendedNotification = new HashMap<>();
    }

    public void attach(String userName) {
        if(!suspendedNotification.containsKey(userName))
            suspendedNotification.put(userName, new LinkedList<>());
    }

    public void dettach(String userName) {
        suspendedNotification.remove(userName);
    }


    public void notify(String userName, String msg){
        if(suspendedNotification.containsKey(userName)){
            suspendedNotification.get(userName).add(msg);
        }
        else {
            suspendedNotification.put(userName, new LinkedList<>());
            suspendedNotification.get(userName).add(msg);
        }
    }

    public List<String> getByUsername(String userName) {
        List<String> ans = suspendedNotification.get(userName);
        suspendedNotification.put(userName, new LinkedList<>());
        return ans;
    }

//    public void notifySuspended(String userName) {
//        Client userSocket = webSockets.get(userName);
//        for(String msg: suspendedNotification.get(userName)){
//            //TODO: SEND MSG VVIA SOCKET
//        }
//
//        suspendedNotification.remove(userName);
//    }
}
