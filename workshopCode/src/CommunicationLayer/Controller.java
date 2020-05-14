package CommunicationLayer;


import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import ServiceLayer.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Controller{

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 8080;
    //Handlers
    private static UsersHandler usersHandler = new UsersHandler();
    private static SearchHandler searchHandler = new SearchHandler();
    private static ShoppingCartHandler cartHandler = new ShoppingCartHandler();
    private static StoreHandler storeHandler = new StoreHandler();
    private static StoreManagerHandler storeManagerHandler = new StoreManagerHandler();
    private static ViewInfoHandler viewInfoHandler = new ViewInfoHandler();
    private static ViewPurchaseHistoryHandler purchaseHistoryHandler = new ViewPurchaseHistoryHandler();

    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int STATUS_OK = 200;
    private static final int STATUS_NOT_OK = 300;
    private static final int STATUS_METHOD_NOT_ALLOWED = 400;

    public static void main(String[] args) throws IOException{
        final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), 1);

        //-----------------------------------------Handle HTML\JS Requests------------------------------------------
        server.createContext("/", he -> {
            String pathToRoot = new File("html\\").getAbsolutePath().concat("\\");
            String path = he.getRequestURI().getPath();
            try {
                path = path.substring(1);
                path = path.replaceAll("//", "/");
                if (path.length() == 0)
                    path = "html\\HomeGuest.html";

                boolean fromFile = new File(pathToRoot + path).exists();
                InputStream in = fromFile ? new FileInputStream(pathToRoot + path)
                        : ClassLoader.getSystemClassLoader().getResourceAsStream(pathToRoot + path);

                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                OutputStream gout =  new DataOutputStream(bout);
                byte[] tmp = new byte[4096];
                int r;
                while ((r=in.read(tmp)) >= 0)
                    gout.write(tmp, 0, r);
                gout.flush();
                gout.close();
                in.close();
                byte[] data = bout.toByteArray();

                if (path.endsWith(".js"))
                    he.getResponseHeaders().set("Content-Type", "text/javascript");
                else if (path.endsWith(".html"))
                    he.getResponseHeaders().set("Content-Type", "text/html");
                else if (path.endsWith(".css"))
                    he.getResponseHeaders().set("Content-Type", "text/css");
                else if (path.endsWith(".json"))
                    he.getResponseHeaders().set("Content-Type", "application/json");
                else if (path.endsWith(".svg"))
                    he.getResponseHeaders().set("Content-Type", "image/svg+xml");
                if (he.getRequestMethod().equals("HEAD")) {
                    he.getResponseHeaders().set("Content-Length", "" + data.length);
                    he.sendResponseHeaders(200, -1);
                    return;
                }

                he.sendResponseHeaders(200, data.length);
                he.getResponseBody().write(data);
                he.getResponseBody().close();
            } catch (NullPointerException t) {
                System.err.println("Error retrieving: " + path);
            } catch (Throwable t) {
                System.err.println("Error retrieving: " + path);
                t.printStackTrace();
            }

        });

        //-----------------------------------------UsersHandler cases------------------------------------------
        //accept: {userName:"", password:"", adminMode: true/false}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/login", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                //JSONObject requestJson =  (JSONObject)parser.parse("{userName: noy, password: 123, adminMode: true}");
                String userName = (requestJson.containsKey("userName")) ? (String)requestJson.get("userName") : null;
                String password = (requestJson.containsKey("password")) ? (String)(requestJson.get("password")) : null;
                boolean adminMode = (requestJson.containsKey("adminMode")) ? (boolean)(requestJson.get("adminMode")) : false;
                String response = usersHandler.login(userName, password, adminMode);
                headers.set("login", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //accept: {userName:"", password:""}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/register", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                //JSONObject requestJson =  (JSONObject)parser.parse("{userName: noy, password: 123}");
                String userName = (requestJson.containsKey("userName")) ? (String)requestJson.get("userName") : null;
                String password = (requestJson.containsKey("password")) ? (String)(requestJson.get("password")) : null;
                String response = usersHandler.register(userName, password);
                headers.set("register", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/logout", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                //JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                String response = usersHandler.logout();
                headers.set("logout", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } finally {
                he.close();
            }
        });

        //accept: {userName: ""}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/addAdmin", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                //JSONObject requestJson =  (JSONObject)parser.parse("{\"userName\":\"noy\"}");
                String userName = (requestJson.containsKey("userName")) ? (String)requestJson.get("userName") : null;
                String response = usersHandler.addAdmin(userName);
                headers.set("addAdmin", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });
        //-----------------------------------------SearchHandler cases------------------------------------------
        //accept: {name: "", category:"", keyWords:["", "", ""]}
        //retrieve: [{name:"", price:"", store:"", description:""}, ..]  OR  {ERROR: msg}
        server.createContext("/tradingSystem/search", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                //JSONObject requestJson =  (JSONObject)parser.parse("{\"userName\":\"noy\"}");
                String name = (requestJson.containsKey("name")) ? (String)requestJson.get("name") : null;
                String category = (requestJson.containsKey("category")) ? (String)requestJson.get("category") : null;
                JSONArray keys = (requestJson.containsKey("keywords")) ? (JSONArray)requestJson.get("keywords") : new JSONArray();
                String[] keywords = new String[keys.size()];
                for(int i = 0; 0 < keys.size(); i++)
                    keywords[i] = (String)keys.get(i);
                String response = searchHandler.searchProduct(name, category, keywords);
                headers.set("search", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //accept: {maxPrice:Integer, minPrice:Integer, category:""}
        //retrieve: [{name:"", price:"", store:"", description:""}, ..]  OR  {ERROR: msg}
        server.createContext("/tradingSystem/filter", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                //JSONObject requestJson =  (JSONObject)parser.parse("{\"userName\":\"noy\"}");
                Integer maxPrice = (requestJson.containsKey("maxPrice")) ? (Integer)requestJson.get("maxPrice") : null;
                Integer minPrice = (requestJson.containsKey("minPrice")) ? (Integer)requestJson.get("minPrice") : null;
                String category = (requestJson.containsKey("category")) ? (String)requestJson.get("category") : null;

                String response = searchHandler.filterResults(maxPrice, minPrice, category);
                headers.set("filter", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });



        //-----------------------------------------StoreHandler cases------------------------------------------
        //accept: {store:"", description:""}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/openNewStore", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                JSONObject requestJson =  (JSONObject)parser.parse("{\"store\":\"Pull&Bear\", \"description\":\"clothes\"}");
                String storeName = (requestJson.containsKey("store")) ? (String)requestJson.get("store") : null;
                String description = (requestJson.containsKey("description")) ? (String)requestJson.get("description") : null;
                String response = storeHandler.openNewStore(storeName, description);
                headers.set("openNewStore", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //accept: {user: "", store:"", amount: ""}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/addStoreOwner", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                //JSONObject requestJson =  (JSONObject)parser.parse("{\"user\":\"noy\", \"store\":\"Pull&Bear\"}");
                String userName = (requestJson.containsKey("user")) ? (String)requestJson.get("user") : null;
                String storeName = (requestJson.containsKey("store")) ? (String)requestJson.get("store") : null;
                String response = storeHandler.openNewStore(userName, storeName);
                headers.set("addStoreOwner", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //accept: {store:"", product: "", price:"", category:"", desc:"", amount:int}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/updateInventory", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                //final String requestMethod = he.getRequestMethod();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));

                String storeName = (requestJson.containsKey("store")) ? (String)requestJson.get("store") : null;
                String userName = (requestJson.containsKey("user")) ? (String)requestJson.get("user") : null;
                int price = (requestJson.containsKey("price")) ? (int)requestJson.get("price") : null;
                String category = (requestJson.containsKey("category")) ? (String)requestJson.get("category") : null;
                String desc = (requestJson.containsKey("desc")) ? (String)requestJson.get("desc") : null;
                int amount = (requestJson.containsKey("amount")) ? (int)requestJson.get("amount") : null;

                String response = storeHandler.UpdateInventory(storeName, userName, price, category, desc, amount);
                headers.set("updateInventory", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //-----------------------------------------ShoppingCartHandler cases------------------------------------------
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/cart", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                //JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                //JSONObject requestJson =  (JSONObject)parser.parse("{\"viewCart\": \"\"}");
                    String e = usersHandler.register("noy", "1234");
                    String d = usersHandler.login("noy", "1234", false);
                    String c = storeHandler.openNewStore("kravitz", "writing tools");
                    storeHandler.openNewStore("Pull&Bear", "clothes");
                    String f = storeHandler.UpdateInventory("kravitz", "pilot", 10, "Clothing", "black pen", 100);
                    storeHandler.UpdateInventory("Pull&Bear", "skirt", 50, "Clothing", "skirt", 5);
                    usersHandler.logout();
                    String a = cartHandler.AddToShoppingBasket("kravitz", "pilot", 5);
                    String b = cartHandler.AddToShoppingBasket("Pull&Bear", "skirt", 2);
                String response = cartHandler.viewCart();
                headers.set("viewCart", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } finally {
                he.close();
            }
        });

        //accept: {store: "", product:"", amount: ""}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/editCart", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                //JSONObject requestJson =  (JSONObject)parser.parse("{store: Pull&Bear, product: skirt, amount: 5}");
                String storeName = (requestJson.containsKey("store"))? ((String)requestJson.get("store")): null;
                String productName = (requestJson.containsKey("product"))? ((String)requestJson.get("product")): null;
                int amount = (requestJson.containsKey("amount"))? ((Integer)requestJson.get("amount")): null;
                String response = cartHandler.editCart(storeName, productName, amount);
                headers.set("editCart", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //accept: {store: "", product:"", amount: ""}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/addToShoppingBasket", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                //JSONObject requestJson =  (JSONObject)parser.parse("{store: Pull&Bear, product: skirt, amount: 5}");
                String storeName = (requestJson.containsKey("store"))? ((String)requestJson.get("store")): null;
                String productName = (requestJson.containsKey("product"))? ((String)requestJson.get("product")): null;
                int amount = (requestJson.containsKey("amount"))? ((Integer)requestJson.get("amount")): null;
                String response = cartHandler.AddToShoppingBasket(storeName, productName, amount);
                headers.set("addToShoppingBasket", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/purchaseCart", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                //JSONParser parser = new JSONParser();
                //JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                String response = cartHandler.purchaseCart();
                headers.set("purchaseCart", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } finally {
                he.close();
            }
        });

        //-----------------------------------------StoreManagerHandler cases------------------------------------------
        //accept: {user: "", store:""}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/addStoreManager", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("user"))? ((String)requestJson.get("user")): null;
                String storeName = (requestJson.containsKey("store"))? ((String)requestJson.get("store")): null;
                String response = storeManagerHandler.addStoreManager(userName, storeName);
                headers.set("addStoreManager", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //accept: {user: "", store:""}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/removeStoreManager", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("user"))? ((String)requestJson.get("user")): null;
                String storeName = (requestJson.containsKey("store"))? ((String)requestJson.get("store")): null;
                String response = storeManagerHandler.removeStoreManager(userName, storeName);
                headers.set("removeStoreManager", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //accept: {user: "", store:"", permission: ["", "",..]}
        //retrieve: {SUCCESS/ERROR: msg}
        server.createContext("/tradingSystem/editPermission", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                String userName = (requestJson.containsKey("user"))? ((String)requestJson.get("user")): null;
                String storeName = (requestJson.containsKey("store"))? ((String)requestJson.get("store")): null;
                JSONArray perm = requestJson.containsKey("permissions")? (JSONArray)requestJson.get("permissions"): new JSONArray();
                List<String> permissions = new LinkedList<>();
                for(int i = 0; i < perm.size(); i++)
                    permissions.add((String)perm.get(i));
                String response = storeManagerHandler.editManagerPermissions(userName, permissions, storeName);
                headers.set("editPermissions", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //-----------------------------------------ViewInfoHandler cases------------------------------------------
        //accept: {store:""}
        //retrieve: {name:"", description:"", products:[{productName:"", price:Integer}]}  OR  {ERROR: msg}
        server.createContext("/tradingSystem/storeInfo", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store"))? ((String)requestJson.get("store")): null;
                String response = viewInfoHandler.viewStoreinfo(storeName);
                headers.set("storeInfo", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });

        //accept: {store:"", product:""}
        //retrieve: {name:"", description:"", price:Integer}  OR  {ERROR: msg}
        server.createContext("/tradingSystem/productInfo", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                String storeName = (requestJson.containsKey("store"))? ((String)requestJson.get("store")): null;
                String productName = (requestJson.containsKey("product"))? ((String)requestJson.get("product")): null;
                String response = viewInfoHandler.viewProductInfo(storeName, productName);
                headers.set("productInfo", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });
        //-----------------------------------------PurchaseHistory cases------------------------------------------

        //-----------------------------------------Other cases----------------------------------------------------
        //retrieve: {name:"", type:"", options:["",..]}
        server.createContext("/tradingSystem/myStores", he -> {
            try {
                final Headers headers = he.getResponseHeaders();
                byte[] requestByte = he.getRequestBody().readAllBytes();
                JSONParser parser = new JSONParser();
                JSONObject requestJson = (JSONObject)parser.parse(new String(requestByte));
                String response = storeHandler.getMyStores();
                headers.set("editPermissions", String.format("application/json; charset=%s", UTF8));
                sendResponse(he, response);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                he.close();
            }
        });
        server.start();
    }

    public static void sendResponse(HttpExchange he, String response) throws IOException {
        final byte[] ResponseBytes = response.getBytes(UTF8);
        he.sendResponseHeaders(STATUS_OK, ResponseBytes.length);
        he.getResponseBody().write(ResponseBytes);
    }

}

