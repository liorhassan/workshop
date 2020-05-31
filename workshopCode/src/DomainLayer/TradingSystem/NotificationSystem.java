package DomainLayer.TradingSystem;



import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class NotificationSystem {

    private HashMap<String, List<String>> notification;
    private HashMap<String, String> connections;
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

//        connections.put(userName, connection);
//        if(notification.containsKey(userName)) {
//            List<String> msgs = notification.get(userName);
//            String msg = "";
//            for(String m: msgs){
//                msg += m + "\n";
//            }
//            //send msg
//            notification.remove(userName);
//        }

    }

    public void dettach(String userName) {
        notification.remove(userName);
//        connections.remove(userName);
    }


    public void notify(String userName, String msg){
        if(notification.containsKey(userName)){
            notification.get(userName).add(msg);
        }
        else {
            notification.put(userName, new LinkedList<>());
            notification.get(userName).add(msg);
        }
//        if(connections.containsKey(userName)) {
//            //send notification via web socket
//        }
//        else{
//            if(notification.containsKey(userName)) {
//                notification.get(userName).add(msg);
//            }
//            else {
//                notification.put(userName, new LinkedList<>());
//                notification.get(userName).add(msg);
//            }
//        }

    }

    public List<String> getByUsername(String userName) {
        if(notification.containsKey(userName)) {
            return notification.get(userName);
        }
        return new LinkedList<>();
    }




}
