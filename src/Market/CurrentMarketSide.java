package Market;

import Price.*;

public class CurrentMarketSide {
    private final Price price;
    private final int volume;

    public CurrentMarketSide(Price price, int volume) {
        this.price = price;
        this.volume = volume;
    }
    @Override
    public String toString(){
        return String.format("%sx%d", (price == null ? "0.00" : price.toString()), volume);
    }
}
