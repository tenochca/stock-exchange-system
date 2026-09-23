package Book;

import Market.*;
import Price.*;
import Tradable.*;
import java.util.*;


public class User implements CurrentMarketObserver {
    private String userId;
    private HashMap<String, TradableDTO> tradables =  new HashMap<>();
    private HashMap<String, CurrentMarketSide[]> currentMarkets = new HashMap<>();


    public User(String userId) {
        setUserId(userId);
    }

    private void setUserId(String user) {
        if (user == null || !user.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Book.User code must be 3 letters, no spaces, no numbers, no special characters.");
        }
        this.userId = user;
    }

    public String getUserId() {
        return userId;
    }

    public void updateTradable (TradableDTO o) {
        if ((o != null)) {
            tradables.put(o.tradableId(), o);
        }
    }

    @Override
    public void updateCurrentMarket(String symbol, CurrentMarketSide buySide, CurrentMarketSide sellSide) {
        CurrentMarketSide[] temp = new CurrentMarketSide[]{buySide, sellSide};
        currentMarkets.put(symbol, temp);
    }

    public String getCurrentMarkets() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, CurrentMarketSide[]> entry : currentMarkets.entrySet()) {
            CurrentMarketSide[] value = entry.getValue();
            CurrentMarketSide buySide = value[0];
            CurrentMarketSide sellSide = value[1];

            sb.append(String.format("%s   %s - %s\n", entry.getKey(), buySide, sellSide));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Book.User Id: ");
        sb.append(userId);
        sb.append("\n");
        for (Map.Entry<String, TradableDTO> entry : tradables.entrySet()) {
            TradableDTO value = entry.getValue();
            sb.append("\t").append(String.format("Product: %s, Price: %s Original Volume: %d, Remaining Volume: %d, Cancelled Volume: %d, Filled Volume: %d, Side: %s, Id: %s",
                    value.product(), value.price(), value.originalVolume(), value.remainingVolume(), value.cancelledVolume(), value.filledVolume(), value.side(), value.tradableId())).append("\n");
        }
        return sb.toString();
    }
}
