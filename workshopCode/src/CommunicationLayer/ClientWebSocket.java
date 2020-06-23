package CommunicationLayer;

import DomainLayer.TradingSystem.NotificationSystem;
import ServiceLayer.SessionHandler;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientWebSocket extends WebSocketServer {

    private static int TCP_PORT = 8088;
    private static ConcurrentHashMap<WebSocket, UUID> connsPerSession;
    private static ConcurrentHashMap<String, WebSocket> usersPerConns;
    private static SessionHandler sessionHandler;

    private static ClientWebSocket ourInstance = new ClientWebSocket();
    public static ClientWebSocket getInstance() {
        return ourInstance;
    }


    public ClientWebSocket() {
        super(new InetSocketAddress(TCP_PORT));
        usersPerConns = new ConcurrentHashMap<>();
        sessionHandler = new SessionHandler();
        connsPerSession = new ConcurrentHashMap<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    JSONObject jo = NotificationSystem.getInstance().getNotifications();
                    while(!usersPerConns.containsKey(jo.get("username").toString())){}
                    send(jo.get("username").toString(),((JSONArray)jo.get("notifications")).toJSONString());
                }
            }
        });
        t.start();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        UUID id = null;
        try {
            id = sessionHandler.openNewSession();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        usersPerConns.put(message, conn);
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
