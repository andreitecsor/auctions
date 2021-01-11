import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

public class Server extends WebSocketServer {

    static ArrayList<String> clients = new ArrayList<String>();
    static ArrayList<Produs> licitatii = new ArrayList<Produs>();
    private final long bidTime = 3000;

    public Server(int port) throws UnknownHostException{
        super(new InetSocketAddress(port));
    }

    public Server(InetSocketAddress address) {
        super(address);
    }

    public Server(int port, Draft_6455 draft) {
        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        conn.send("Welcome to the server !"); //This method sends a message to the new client
        if(licitatii.isEmpty()){
            conn.send("No biddings available");
        }
        else{
            conn.send(licitatii.toString());
        }
        //TODO: Edit this to show the name
        broadcast("new connection: " + handshake
                .getResourceDescriptor()); //This method sends a message to all clients connected
        //TODO: Edit this to show the name
        System.out.println(
                conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        //TODO: Edit this to show the name
        broadcast(conn + " has left the room!");
        //TODO: Edit this to show the name
        System.out.println(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if(message.startsWith("!")){
            message = message.substring(1);
            if(clients.contains(message)){
                conn.send("Name already taken");
                conn.close();
            }
            else {
                clients.add(message);
                System.out.println(clients.toString());
            }
        }
        else if (message.startsWith("@")){
            message=message.substring(1);

                String name = message.substring(message.indexOf("(") + 1);
                name = name.substring(0, name.indexOf(")"));
//                System.out.println(name);
                String numeLicitant = message.substring(0,message.indexOf("("));
//                System.out.println(numeLicitant);
                float pretStart = Float.parseFloat(message.substring(message.indexOf(")")+1));
//                System.out.println(pretStart);
            boolean check = true;
            for(int i=0;i<licitatii.size();i++){
                if(licitatii.get(i).nume.equals(name)){
                    check = false;
                }
            }
            if(check) {
                Produs p = new Produs(name, numeLicitant, pretStart);
                licitatii.add(p);
                broadcast("A new item has been added for bidding: " + p.toString());
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                licitatii.remove(p);
                                broadcast("The bidding for " + p.nume + " has finished!");
                            }
                        },
                        bidTime
                );
            }
            else{
                conn.send("Bidding already exists");
            }
//                System.out.println(new Produs(name,numeLicitant,pretStart));
        }
        else if(message.equals("/bids")){
            conn.send(licitatii.toString());
            System.out.println("hau "+ licitatii.toString());
        }
        else if(message.startsWith("/bid")){
            boolean sent = false;
            String[] values = message.split(" ");
            for(int i=0;i<values.length;i++) System.out.println(values[i]);
            for(int i=0;i<licitatii.size();i++){
                if(licitatii.get(i).nume.equals(values[1])) {
                    if (licitatii.get(i).pretCurent < Float.parseFloat(values[2])){
                        licitatii.get(i).pretCurent = Float.parseFloat(values[2]);
                        conn.send("Bid successful");
                        sent = true;
                    }
                    else{
                        conn.send("Your bid must be higher than the current price!");
                        sent = true;
                    }
                }
            }
            if(!sent){
                conn.send("No bidding found!");
            }
        }
        else{
            broadcast(message);
            System.out.println(conn + ": " + message);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        broadcast(message.array());
        System.out.println(conn + ": " + message);
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        int port = 8887;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ex) {
        }
        Server s = new Server(port);
        s.start();
        System.out.println("Server started on port: " + s.getPort());

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysin.readLine();
            s.broadcast(in);
            if (in.equals("exit")) {
                s.stop(1000);
                break;
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

}

