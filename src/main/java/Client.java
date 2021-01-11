import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Scanner;

public class Client extends WebSocketClient {

    String name;

    public Client(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public Client(URI serverURI) {
        super(serverURI);
        System.out.println("Please enter a name: ");
        Scanner scanner = new Scanner(System.in);
        this.name = scanner.nextLine();
    }

    public Client(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("opened connection");
        this.send("!"+this.name);
        this.send("@mortiimei(sugpula123)123.23");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    public static void main(String[] args) throws URISyntaxException {
        Client c = new Client(new URI(
                "ws://localhost:8887"));
        c.connect();
        Scanner scanner = new Scanner(System.in);
        while(true){
            String msg = scanner.nextLine();
            if(msg.equalsIgnoreCase("/list")) {
                System.out.println("Enter the name of the product");
                String name = scanner.nextLine();
                System.out.println("Enter the starting price");
                float price = Float.parseFloat(scanner.nextLine());
                c.send("@" + c.name + "(" + name + ")" + price);
            }
            else {
                c.send(msg);
            }
        }
    }

}