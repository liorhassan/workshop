package CommunicationLayer;
import DomainLayer.TradingSystem.NotificationSystem;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.List;


@ServerEndpoint("/endpoint")
public class Client {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen::" + session.getId());
        String username = "";
        List<String> notifications = NotificationSystem.getInstance().getByUsername(username);
        while(true){
            while(!notifications.isEmpty()) {
                //TODO: SEND MSG
            }
             notifications = NotificationSystem.getInstance().getByUsername(username);
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("onClose::" +  session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {

    }

}
