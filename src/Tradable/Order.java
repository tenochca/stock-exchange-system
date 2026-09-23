package Tradable;

import Exceptions.*;
import Price.*;
import Tradable.*;

public class Order implements Tradable {
    private String user; //3-letter code no spaces, no numbers, no special characters
    private String product; //stock sym- 1 to 5 letters/numbers, period allowed '.'
    private Price price;
    private BookSide side; //BUY or SELL
    private int originalVolume; //The quantity of the stock being ordered
    private int remainingVolume;
    private int cancelledVolume = 0;
    private int filledVolume = 0;
    private String id; //unique to each order

    public Order(String user, String product, Price price, int originalVolume, BookSide side) throws InvalidPriceException, InvalidBookSideException, InvalidVolumeException {
        setUser(user);
        setProduct(product);
        setPrice(price);
        setSide(side);
        setOriginalVolume(originalVolume);
        this.remainingVolume = originalVolume;
        this.id = user + product + price.toString() + System.nanoTime();
    }

    public String getUser() {
        return user;
    }

    private void setUser(String user) {
        if (user == null || !user.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Book.User code must be 3 letters, no spaces, no numbers, no special characters.");
        }
        this.user = user;
    }

    public String getProduct() {
        return product;
    }

    private void setProduct(String product) {
        if (product == null || !product.matches("[A-Z0-9.]{1,5}")) {
            throw new IllegalArgumentException("Product symbol must be 1 to 5 characters, can include letters, numbers, and a period.");
        }
        this.product = product;
    }

    public Price getPrice() {
        return price;
    }

    private void setPrice(Price price) throws InvalidPriceException {
        if (price == null) {
            throw new InvalidPriceException("Price.Price cannot be null");
        }
        this.price = price;
    }

    public BookSide getSide() {
        return side;
    }

    private void setSide(BookSide side) throws InvalidBookSideException {
        if (side == null) {
            throw new InvalidBookSideException("Side cannot be null.");
        }
        this.side = side;
    }

    public int getOriginalVolume() {
        return originalVolume;
    }

    private void setOriginalVolume(int originalVolume) throws InvalidVolumeException {
        if (originalVolume <= 0 || originalVolume >= 10000) {
            throw new InvalidVolumeException("Original volume must be greater than 0 and less than 10,000");
        }
        this.originalVolume = originalVolume;
    }

    public int getRemainingVolume() {
        return remainingVolume;
    }

    public void setRemainingVolume(int remainingVolume) {
        this.remainingVolume = remainingVolume;
    }

    public int getCancelledVolume() {
        return cancelledVolume;
    }

    public void setCancelledVolume(int cancelledVolume) {
        this.cancelledVolume = cancelledVolume;
    }

    public int getFilledVolume() {
        return filledVolume;
    }

    public void setFilledVolume(int filledVolume) {
        this.filledVolume = filledVolume;
    }

    public String getId() {
        return id;
    }

    private void setId(String user, String product, Price price) {
        this.id = user + product + price.toString() + System.nanoTime();
    }

    @Override
    public String toString() {
        return String.format("%s %s order: %s at %s, Orig Vol: %d, Rem Vol: %d, Fill Vol: %d, CXL Vol: %d, ID: %s",
                user, side, product, price, originalVolume, remainingVolume, filledVolume, cancelledVolume, id);
    }

    public TradableDTO makeTradableDTO() {
        return new TradableDTO(user, product, price, originalVolume, remainingVolume,
                cancelledVolume, filledVolume, side, id);
    }
}
