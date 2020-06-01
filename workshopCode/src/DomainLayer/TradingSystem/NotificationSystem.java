package DomainLayer.TradingSystem;



import CommunicationLayer.ClientWebSocket;

import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class NotificationSystem {

    private HashMap<String, List<String>> notification;
    private List<String> loggedIn;
    private static NotificationSystem ourInstance = new NotificationSystem();
    public static NotificationSystem getInstance() {
        return ourInstance;
    }


    public NotificationSystem(){
        this.notification = new HashMap<>();
        this.loggedIn = new LinkedList<>();
    }

    public void attach(String userName) {
        if(notification.containsKey(userName)) {
            List<String> msgs = notification.get(userName);
            String msg = "";
            for(String m: msgs){
                msg += m + "\n";
            }
            ClientWebSocket.getInstance().send(userName, msg);
        }
        loggedIn.add(userName);
        notification.put(userName, new LinkedList<>());

    }

    public void dettach(String userName) {
        notification.remove(userName);
        loggedIn.remove(userName);
    }


    public void notify(String userName, String msg){
        if(loggedIn.contains(userName)) {
            ClientWebSocket.getInstance().send(userName, msg);
        }
        else{
            if(notification.containsKey(userName)) {
                notification.get(userName).add(msg);
            }
            else {
                notification.put(userName, new LinkedList<>());
                notification.get(userName).add(msg);
            }
        }

    }

}
