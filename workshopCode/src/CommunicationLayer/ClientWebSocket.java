package CommunicationLayer;

import DomainLayer.TradingSystem.NotificationSystem;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class ClientWebSocket extends WebSocketServer {

    private static int TCP_PORT = 8088;
    private static String username;
    private Set<WebSocket> conns;

    public ClientWebSocket() {
        super(new InetSocketAddress(TCP_PORT));
        conns = new HashSet<>();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
//        conns.add(conn);
        conn = conn;
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//        conns.remove(conn);
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from client: " + message);
//        this.username= message;
//        String notification = NotificationSystem.getInstance().getByUsername(this.username);
//        while(NotificationSystem.getInstance().isLoggedIn(this.username)){
//            if(notification != null) {
//                conn.send(notification);
//            }
//            notification = NotificationSystem.getInstance().getByUsername(this.username);
//        }
        new Thread(() ->
        {
            this.username= message;
            String notification = NotificationSystem.getInstance().getByUsername(this.username);
            while(NotificationSystem.getInstance().isLoggedIn(this.username)) {
                if (notification != null) {
                    conn.send(notification);
                }
                notification = NotificationSystem.getInstance().getByUsername(this.username);
            }
        }).start();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onStart() {

    }

}
