package Book;

import Exceptions.*;
import Price.*;
import Tradable.*;

public class Quote {
    private String user;
    private String product;
    private QuoteSide buySide;
    private QuoteSide sellSide;

    public Quote(String symbol, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume, String userName)
            throws InvalidPriceException, InvalidBookSideException, InvalidVolumeException {
        this.user = userName;
        this.product = symbol;
        this.buySide = new QuoteSide(userName, symbol, buyPrice, BookSide.BUY, buyVolume);
        this.sellSide = new QuoteSide(userName, symbol, sellPrice, BookSide.SELL, sellVolume);
    }

    public QuoteSide getQuoteSide(BookSide sideIn) {
        if (sideIn == BookSide.BUY) {
            return buySide;
        }
        if (sideIn == BookSide.SELL) {
            return sellSide;
        }
        else {
            throw new NullPointerException("Tradable.QuoteSide cannot be Null");
        }
    }

    public String getSymbol() {
        return product;
    }
    public String getUser() {
        return user;
    }
}
