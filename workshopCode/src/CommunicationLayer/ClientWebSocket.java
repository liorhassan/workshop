package CommunicationLayer;

import DomainLayer.TradingSystem.NotificationSystem;
import ServiceLayer.SessionHandler;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientWebSocket extends WebSocketServer {

    private static int TCP_PORT = 8088;
    private static String username;
    private static ConcurrentHashMap<WebSocket, UUID> connsPerSession;
    private static HashMap<WebSocket, String> connsPerUser;
    private static HashMap<String, WebSocket> usersPerConns;
    private static SessionHandler sessionHandler;

    private static ClientWebSocket ourInstance = new ClientWebSocket();
    public static ClientWebSocket getInstance() {
        return ourInstance;
    }


    public ClientWebSocket() {
        super(new InetSocketAddress(TCP_PORT));
        connsPerUser = new HashMap<>();
        usersPerConns = new HashMap<>();
        sessionHandler = new SessionHandler();
        connsPerSession = new ConcurrentHashMap<>();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        UUID id = sessionHandler.openNewSession();
        connsPerSession.put(conn,id);
        JSONObject sid = new JSONObject();
        sid.put("session_id",id.toString());
        conn.send(sid.toJSONString());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        sessionHandler.closeSession(connsPerSession.get(conn));
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
