package Book;
import Market.*;
import Exceptions.*;
import Price.*;
import Tradable.*;

import javax.swing.plaf.*;
import javax.xml.crypto.*;

public class ProductBook {
    private String product;
    private ProductBookSide buySide;
    private ProductBookSide sellSide;

    public ProductBook(String product) {
        if (product == null || product.isEmpty()) {
            throw new NullPointerException("Cannot make a null Product Book or with empty string");
        }
        this.product = product;
        this.buySide = new ProductBookSide(BookSide.BUY);
        this.sellSide = new ProductBookSide(BookSide.SELL);
    }

    public TradableDTO add(Tradable t) throws InvalidPriceException, DataValidationException {
        if (t == null) {
            throw new NullPointerException("Tradable.Tradable cannot be null");
        }
        if (t.getSide() == BookSide.BUY) { //if tradable is for BUY
            TradableDTO buySideDTO = buySide.add(t); //add to buy side
            tryTrade(); //try the trade
            updateMarket();
            return buySideDTO; //return the DTO
        }
        if (t.getSide() == BookSide.SELL) { //if tradable is for SELL side
            TradableDTO sellSideDTO = sellSide.add(t); //add to sell side
            tryTrade(); //try the trade
            updateMarket();
            return sellSideDTO;
        }
        return null;
    }

    public TradableDTO[] add(Quote qte) throws InvalidPriceException, DataValidationException {
        if (qte == null) {
            throw new NullPointerException("Book.Quote cannot be null");
        }
        buySide.removeQuotesForUser(qte.getUser());
        sellSide.removeQuotesForUser(qte.getUser());

        TradableDTO buySideDTO = buySide.add(qte.getQuoteSide(BookSide.BUY));
        TradableDTO sellSideDTO = sellSide.add(qte.getQuoteSide(BookSide.SELL));

        tryTrade();

        return new TradableDTO[]{buySideDTO, sellSideDTO};
    }

    public TradableDTO cancel(BookSide side, String orderId) throws DataValidationException, InvalidPriceException {
        if (side == BookSide.BUY) {
            buySide.cancel(orderId);
            updateMarket();
        }
        if (side == BookSide.SELL) {
            sellSide.cancel(orderId);
            updateMarket();
        }
        if (orderId == null) {
            throw new NullPointerException("orderId cannot be null");
        }
        return null;
    }

    public TradableDTO[] removeQuotesForUser(String userName) throws DataValidationException, InvalidPriceException {
        if (userName == null) {
            throw new NullPointerException("Username cannot be null");
        }
        TradableDTO buySideDTO = buySide.removeQuotesForUser(userName);
        TradableDTO sellSideDTO = sellSide.removeQuotesForUser(userName);

        updateMarket();

        return new TradableDTO[]{buySideDTO, sellSideDTO};
    }

    public String getTopOfBookString(BookSide side) {
        if (side == BookSide.BUY)
            return "Top of BUY book: " + (buySide.topOfBookPrice() == null ? "$0.00" : buySide.topOfBookPrice()) + " x " + buySide.topOfBookVolume();
        else
            return "Top of SELL book: " +
                    (sellSide.topOfBookPrice() == null ? "$0.00" : sellSide.topOfBookPrice())
                    + " x " + sellSide.topOfBookVolume();
    }

    public void tryTrade() throws InvalidPriceException, DataValidationException {
        int buyVolume = buySide.topOfBookVolume();
        int sellVolume = sellSide.topOfBookVolume();
        int totalToTrade = Math.max(buyVolume, sellVolume);

        //System.out.println("Initial buyVolume: " + buyVolume + ", sellVolume: " + sellVolume);

        while (totalToTrade > 0) { // loop while we have stuff to trade
            Price topBuyPrice = buySide.topOfBookPrice(); // Get the top BUY price
            Price topSellPrice = sellSide.topOfBookPrice(); // Get the top SELL price
            if (topBuyPrice == null || topSellPrice == null) {
                return;
            }
            if (topSellPrice.greaterThan(topBuyPrice)) { // Check if top SELL price is greater than top BUY price
                return;
            } else {
                int toTrade = Math.min(buyVolume, sellVolume);
                //System.out.println("Trading volume: " + toTrade + " at price: " + topBuyPrice);
                buySide.tradeOut(topBuyPrice, toTrade);
                sellSide.tradeOut(topBuyPrice, toTrade);
                totalToTrade -= toTrade;
                buyVolume = buySide.topOfBookVolume();
                sellVolume = sellSide.topOfBookVolume();
                totalToTrade = Math.max(buyVolume, sellVolume);
                //System.out.println("Remaining buyVolume: " + buyVolume + ", sellVolume: " + sellVolume);
            }
        }
    }


    private void updateMarket() throws InvalidPriceException {
        Price topBuyPrice = buySide.topOfBookPrice();
        int topBuyVolume = buySide.topOfBookVolume();

        Price topSellPrice = sellSide.topOfBookPrice();
        int topSellVolume = sellSide.topOfBookVolume();

        CurrentMarketTracker.getInstance().updateMarket(product, topBuyPrice, topBuyVolume, topSellPrice, topSellVolume);
    }

    @Override
    public String toString() {
        return "\nProduct: " + this.product + "\n" +
                "Side: BUY\n" + buySide.toString() + "\n" +
                "Side: SELL\n" + sellSide.toString() + "\n";
    }


}
