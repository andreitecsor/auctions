import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private String name;
    private String bidderName;
    private float startingPrice;
    private float currentPrice;
    private List<InetSocketAddress> bidders = new ArrayList<>();

    public Product(String name, String bidderName, float startingPrice) {
        this.name = name;
        this.bidderName = bidderName;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public float getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(float startingPrice) {
        this.startingPrice = startingPrice;
    }

    public float getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(float currentPrice) {
        this.currentPrice = currentPrice;
    }

    public List<InetSocketAddress> getBidders() {
        return bidders;
    }

    public void setBidders(List<InetSocketAddress> bidders) {
        this.bidders = bidders;
    }

    public void addBidder(InetSocketAddress newBidder) {
        bidders.add(newBidder);
    }

    public void deleteBidder(InetSocketAddress bidder) {
        bidders.remove(bidder);
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", bidderName='" + bidderName + '\'' +
                ", startingPrice=" + startingPrice +
                ", currentPrice=" + currentPrice +
                '}';
    }
}
