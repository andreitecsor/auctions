public class Product {
    private String name;
    private String bidderName;
    private float startingPrice;
    private float currentPrice;

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
