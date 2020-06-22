package ExternalSystems;

import DomainLayer.TradingSystem.Models.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Map;


public class PaymentCollectionStub implements PaymentCollection{

    public boolean pay(double totalPrice, User user){
        if(totalPrice >= 1000)
            return false;
        return true;
    }

    public int pay(Hashtable<String, String> data) {

        var handshake = new Hashtable<String, String>();
        handshake.put("action_type", "handshake");
        String response = postMsg(handshake);
        if(!response.equals("OK")){
                return -1;
        }

        try{
            int transId = Integer.parseInt(postMsg(data));
            return transId;
        }
        catch(Exception e){
            return -1;
        }

    }


    public int cancelPayment(int transactionId) {

        var handshake = new Hashtable<String, String>();
        handshake.put("action_type", "handshake");
        String response = postMsg(handshake);
        if(!response.equals("OK")){
            return -1;
        }

        var data = new Hashtable<String, String>();
        data.put("action_type", "cancel_pay");
        data.put("transaction_id", String.valueOf(transactionId));
        try{
            String ans = postMsg(data);
            int transId = ans.equals("") ? -1 : Integer.parseInt(ans);
            return transId;
        }
        catch(Exception e){
            return -1;
        }

    }


    private String postMsg(Hashtable<String, String> dict){
        try{

            StringBuilder postData = new StringBuilder();
            for(Map.Entry<String,String> entry : dict.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            byte[] postDataBytes = postData.toString().getBytes("UTF-8");
            URL url = new URL("https://cs-bgu-wsep.herokuapp.com/");

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);


            Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0;)
                sb.append((char)c);
            String response = sb.toString();

//            System.out.println(dict.toString());
//            System.out.println(response);
            return response;
        }
        catch (Exception e){
            return "";
        }
    }
}

