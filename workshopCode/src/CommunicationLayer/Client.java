package CommunicationLayer;
import DomainLayer.TradingSystem.NotificationSystem;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


@ServerEndpoint("/socketEndPoint")
public class Client {

    List<Session> clients = new LinkedList<>(); //???????????
    String ID;
    String username;


    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen::" + session.getId());
        this.ID = session.getId();
//        String username= (String) session.getUserProperties().get("username");
//        String notification = NotificationSystem.getInstance().getByUsername(username);
//        while(true){
//            if(notification != null) {
//                session.getBasicRemote().sendText(notification);
//            }
//             notification = NotificationSystem.getInstance().getByUsername(username);
//        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("onClose::" +  session.getId());
    }


    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        this.username= message;
        String notification = NotificationSystem.getInstance().getByUsername(this.username);
        while(NotificationSystem.getInstance().isLoggedIn(this.username)){
            if(notification != null) {
                session.getBasicRemote().sendText(notification);
            }
            notification = NotificationSystem.getInstance().getByUsername(this.username);
        }
    }


}

//JS!!!!!
//var WebSocket = new WebSocket( 'ws://localhost:8080/webSocket');
//webSocket.onopen = function () { webSocket.send("userName")};
//webSocket.onmessage = function(msg) { };
//disconnect() { webSocket.close}
