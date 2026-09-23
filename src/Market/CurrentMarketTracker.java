package Market;

import Exceptions.*;
import Price.*;

public final class CurrentMarketTracker {
    private static CurrentMarketTracker instance;

    private CurrentMarketTracker() {}

    public static CurrentMarketTracker getInstance() {
        if (instance == null) {
            instance = new CurrentMarketTracker();
        }
        return instance;
    }

    public void updateMarket(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws InvalidPriceException {
        Price marketWidth;
        if (buyPrice == null || sellPrice == null) {
            marketWidth = PriceFactory.makePrice(0);
            //buyPrice = PriceFactory.makePrice(0);
            //sellPrice = PriceFactory.makePrice(0);
        } else {
            marketWidth = sellPrice.subtract(buyPrice);
        }

        CurrentMarketSide buyMarketSide = new CurrentMarketSide(buyPrice, buyVolume);
        CurrentMarketSide sellMarketSide = new CurrentMarketSide(sellPrice, sellVolume);

        String currentMarketString = "*********** Current Market ***********\n" +
                String.format("* %s  %s - %s [%s]", symbol, buyMarketSide, sellMarketSide, marketWidth) +
                "\n**************************************";
        System.out.println(currentMarketString);

        //TODO call currentMarketPublisher acceptCurrentMarket method
        CurrentMarketPublisher.getInstance().acceptCurrentMarket(symbol, buyMarketSide, sellMarketSide);
    }
}
