import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Server extends WebSocketServer {

    static ArrayList<String> clientList = new ArrayList<String>();
    static ArrayList<Product> auctionList = new ArrayList<Product>();
    //TODO: Change this
    private final long bidTime = Long.MAX_VALUE;

    public Server(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

//    public Server(InetSocketAddress address) {
//        super(address);
//    }

//    public Server(int port, Draft_6455 draft) {
//        super(new InetSocketAddress(port), Collections.<Draft>singletonList(draft));
//    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server !");
        if (auctionList.isEmpty()) {
            conn.send("No auctions available");
        } else {
            conn.send(auctionList.toString());
        }

        //TODO: Edit this to show the name
        broadcast("new connection: " + handshake
                .getResourceDescriptor()); //This method sends a message to all clients connected
        //TODO: Edit this to show the name
        System.out.println(
                conn.getRemoteSocketAddress() + " entered the room!");
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
        //Connecting a new client - checking name
        System.out.println(conn.getRemoteSocketAddress());
        if (message.startsWith("!")) {
            message = message.substring(1);
            if (clientList.contains(message)) {
                conn.send("Name already taken");
                conn.close();

            } else {
                clientList.add(message);
                System.out.println(clientList.toString());
            }
        }
        //Managing a new bid request
        if (message.startsWith("@")) {
            message = message.substring(1);

            String productName = message.substring(message.indexOf("(") + 1);
            productName = productName.substring(0, productName.indexOf(")"));
            String bidderName = message.substring(0, message.indexOf("("));

            float startingPrice = Float.parseFloat(message.substring(message.indexOf(")") + 1));
            boolean check = true;
            for (int i = 0; i < auctionList.size(); i++) {
                if (auctionList.get(i).getName().equals(productName)) {
                    check = false;
                }
            }

            if (check) {
                Product product = new Product(productName, bidderName, startingPrice);
                auctionList.add(product);
                broadcast("A new item has been added for bidding: " + product.toString());
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                auctionList.remove(product);
                                broadcast("The bidding for " + product.getName() + " has finished!");
                            }
                        },
                        bidTime
                );
            } else {
                conn.send("Auction already exists");
            }

        }
        if (message.equals("/auctions")) {
            conn.send(auctionList.toString());
        }
        if (message.startsWith("/bid")) {
            try {
                boolean sent = false;
                String[] values = message.split(" ");
                if (values.length > 0) {
//                    for (int i = 0; i < values.length; i++) {
//                        System.out.println(values[i]);
//                    }
                    for (int i = 0; i < auctionList.size(); i++) {
                        if (auctionList.get(i).getName().equals(values[1])) {
                            if (auctionList.get(i).getCurrentPrice() < Float.parseFloat(values[2])) {
                                auctionList.get(i).setCurrentPrice(Float.parseFloat(values[2]));
                                conn.send("Bid successful");
                            } else {
                                conn.send("Your bid must be higher than the current price!");
                            }
                            sent = true;
                        }
                    }
                } else {
                    conn.send("Invalid command! You should use: /bid [productName] [amount]");
                }

                if (!sent) {
                    conn.send("No auction found!");
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
//        else {
//            broadcast(message);
//            //TODO: replace conn with client name or delete it
//            System.out.println(conn + ": " + message);
//        }
    }

//    @Override
//    public void onMessage(WebSocket conn, ByteBuffer message) {
//        broadcast(message.array());
//        System.out.println(conn + ": " + message);
//    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        int port = 8887;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ex) {
            System.out.println("Invalid port");
        }
        Server server = new Server(port);
        server.start();
        System.out.println("Server started on port: " + server.getPort());

        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysIn.readLine();
            server.broadcast(in);
            if (in.equals("exit")) {
                server.stop(1000);
                break;
            }
        }
    }

}

