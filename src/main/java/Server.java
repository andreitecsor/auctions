import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server extends WebSocketServer {

    private Map<InetSocketAddress, String> clientMap = new HashMap<>();

    static ArrayList<String> clientNameList = new ArrayList<>();
    static ArrayList<Product> auctionList = new ArrayList<>();

    //TODO: Change this
    private final long bidTime = Long.MAX_VALUE;

    public Server(int port) {
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
        setConnectionLostTimeout(1000);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server !");
        if (auctionList.isEmpty()) {
            conn.send("No auctions available");
        } else {
            conn.send(auctionList.toString());
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(clientMap.get(conn.getRemoteSocketAddress()) + " has left the room!");
        System.out.println(clientMap.get(conn.getRemoteSocketAddress()) + " has left the room!");
        clientNameList.remove(clientMap.get(conn.getRemoteSocketAddress()));
        clientMap.remove(conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //Connecting a new client - checking name
        System.out.println(conn.getRemoteSocketAddress());
        if (message.startsWith("!")) {
            message = message.substring(1);
            if (clientNameList.contains(message)) {
                conn.send("Name already taken");
                conn.close();
            } else {
                clientNameList.add(message);
                clientMap.put(conn.getRemoteSocketAddress(), message);
                broadcast("new connection: " + clientMap.get(conn.getRemoteSocketAddress()));
                System.out.println(clientMap.get(conn.getRemoteSocketAddress()) + " entered the room!");
            }
            return;
        }

        //Managing a new bid request
        if (message.startsWith("@")) {
            message = message.substring(1);

            String productName = message.substring(message.indexOf("(") + 1);
            productName = productName.substring(0, productName.indexOf(")"));
            String ownerName = message.substring(0, message.indexOf("("));

            float startingPrice = Float.parseFloat(message.substring(message.indexOf(")") + 1));
            boolean check = true;
            for (int i = 0; i < auctionList.size(); i++) {
                if (auctionList.get(i).getName().equals(productName)) {
                    check = false;
                }
            }

            if (check) {
                Product product = new Product(productName, ownerName, startingPrice);
                product.addBidder(conn.getRemoteSocketAddress());
                auctionList.add(product);
                broadcast("A new item has been added for auction: " + product.toString());
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
            return;
        }

        if (message.equals("/auctions")) {
            conn.send(auctionList.toString());
            return;
        }
        if (message.startsWith("/bid")) {
            try {
                boolean sent = false;
                String[] values = message.split(" ");
                if (values.length > 0) {
                    for (int i = 0; i < auctionList.size(); i++) {
                        Product currentProduct = auctionList.get(i);
                        if (currentProduct.getName().equals(values[1])) {
                            if (currentProduct.getCurrentPrice() < Float.parseFloat(values[2])) {
                                currentProduct.setCurrentPrice(Float.parseFloat(values[2]));
                                if (!currentProduct.getBidders().contains(conn.getRemoteSocketAddress())) {
                                    currentProduct.addBidder(conn.getRemoteSocketAddress());
                                }
                                conn.send("Bid successful");

                                for (WebSocket webSocket : getConnections()) {
                                    if (currentProduct.getBidders().contains(webSocket.getRemoteSocketAddress())) {
                                        webSocket.send(clientMap.get(conn.getRemoteSocketAddress())
                                                + " placed a new bid on "
                                                + currentProduct.getName()
                                                + ": "
                                                + currentProduct.getCurrentPrice());
                                    }
                                }
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
                return;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
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
//            System.out.println("Invalid port");
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

