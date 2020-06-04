package CommunicationLayer;

import DomainLayer.TradingSystem.NotificationSystem;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;

public class ClientWebSocket extends WebSocketServer {

    private static int TCP_PORT = 8088;
    private static String username;
    private static HashMap<WebSocket, String> connsPerUser;
    private static HashMap<String, WebSocket> usersPerConns;

    private static ClientWebSocket ourInstance = new ClientWebSocket();
    public static ClientWebSocket getInstance() {
        return ourInstance;
    }


    public ClientWebSocket() {
        super(new InetSocketAddress(TCP_PORT));
        connsPerUser = new HashMap<>();
        usersPerConns = new HashMap<>();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        conn.send("[\"shauli\",\"shauli\"]");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//        NotificationSystem.getInstance().dettach(connsPerUser.get(conn));
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from client: " + message);
        this.username= message;
        connsPerUser.put(conn, message);
        usersPerConns.put(message, conn);
        NotificationSystem.getInstance().attach(message);
    }

    public void send(String userName, String msg) {
        if(usersPerConns.containsKey(userName))
            usersPerConns.get(userName).send(msg);
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
