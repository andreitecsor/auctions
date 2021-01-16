import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Client extends WebSocketClient {

    String name;

    public Client(URI serverURI) {
        super(serverURI);
        System.out.println("Please enter a name: ");
        Scanner scanner = new Scanner(System.in);
        this.name = scanner.nextLine();
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("Opened connection");
        System.out.println("Commands: \n /auctions -> see all the active auctions \n /bid [product name] [amount] -> to bid for a product \n /list -> to list a new auction");
        this.send("!" + this.name);
    }

    @Override
    public void onMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed.");
    }

    @Override
    public void onError(Exception ex) {
        System.out.println(ex.getMessage());
    }

    public static void main(String[] args) throws URISyntaxException {
        Client client = new Client(new URI("ws://localhost:8887"));
        client.connect();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("/list")) {
                System.out.println("Enter the name of the product");
                String productName = scanner.nextLine();
                System.out.println("Enter the starting price");
                float productPrice = Float.parseFloat(scanner.nextLine());
                client.send("@" + client.name + "(" + productName + ")" + productPrice);
            } else {
                client.send(input);
            }
        }
    }

}