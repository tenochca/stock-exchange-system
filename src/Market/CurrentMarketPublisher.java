package Market;

import java.util.*;

public final class CurrentMarketPublisher {
    private static CurrentMarketPublisher instance;

    private CurrentMarketPublisher(){}

    public static CurrentMarketPublisher getInstance() {
        if (instance == null) {
            return new CurrentMarketPublisher();
        }
        return instance;
    }

    private static HashMap<String, ArrayList<CurrentMarketObserver>> filters = new HashMap<>();

    public void subscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) {
        if (filters.get(symbol) == null) {
            filters.put(symbol, new ArrayList<>());
        }
        filters.get(symbol).add(cmo);
    }

    public void unSubscribeCurrentMarket(String symbol, CurrentMarketObserver cmo) {
        if (filters.get(symbol) == null) {
            return;
        }
        filters.get(symbol).remove(cmo);
    }

    void acceptCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        if (filters.get(symbol) == null) {
            return;
        }
        ArrayList<CurrentMarketObserver> observers = filters.get(symbol);
        for (CurrentMarketObserver observer : observers) {
            observer.updateCurrentMarket(symbol, buySide, sellSide);
        }
    }
}
