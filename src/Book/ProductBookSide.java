package Book;

import Exceptions.*;
import Price.*;
import Tradable.*;

import java.util.*;

public class ProductBookSide {
    private final BookSide side;
    private final TreeMap<Price, ArrayList<Tradable>> bookEntries;

    public ProductBookSide(BookSide side) {
        this.side = side;
        this.bookEntries = new TreeMap<>();
    }

    public TradableDTO add(Tradable o) throws DataValidationException {
        Price tradablePrice = o.getPrice();
        if (!bookEntries.containsKey(tradablePrice)) { //case the entries does not contain this price
            bookEntries.put(tradablePrice, new ArrayList<>()); //add the price as a key with an empty array list as the value
            bookEntries.get(tradablePrice).add(o); //then also add the Tradable.Tradable to the key's values
        } else {
            bookEntries.get(tradablePrice).add(o); //else if the price is already in the entries add the Tradable.Tradable to the values
        }
        UserManager.getInstance().updateTradable(o.getUser(), o.makeTradableDTO());
        return o.makeTradableDTO();
    }

    public TradableDTO cancel(String tradableId) throws DataValidationException {
        for (ArrayList<Tradable> tradableArrayList : bookEntries.values()) { // for each ArrayList<Tradable.Tradable> in values
            Iterator<Tradable> iterator = tradableArrayList.iterator();
            while (iterator.hasNext()) { // for each item in the ArrayList<Tradable.Tradable>
                Tradable tradable = iterator.next();
                if (tradable.getId().equals(tradableId)) { // check the id
                    iterator.remove(); // remove the tradable from the ArrayList
                    tradable.setCancelledVolume(tradable.getCancelledVolume() + tradable.getRemainingVolume());
                    tradable.setRemainingVolume(0);
                    if (tradableArrayList.isEmpty()) {
                        bookEntries.values().remove(tradableArrayList); // remove the empty ArrayList from the map
                    }
                    UserManager.getInstance().updateTradable(tradable.getUser(), tradable.makeTradableDTO());
                    return tradable.makeTradableDTO();
                }
            }
        }
        return null;
    }


    public TradableDTO removeQuotesForUser(String userName) throws DataValidationException {
        for (ArrayList<Tradable> tradableArrayList : bookEntries.values()) { //for each ArrayList in the values
            for (Tradable tradable : tradableArrayList) { //for each tradable in the array list
                if (tradable.getUser().equals(userName)) { //check for username
                    TradableDTO cancelledTradable = cancel(tradable.getId()); //cancell if found
                    if (tradableArrayList.isEmpty()) { //if no matching entry is found
                        bookEntries.values().remove(tradableArrayList); //remove the list
                    }
                    UserManager.getInstance().updateTradable(tradable.getUser(), tradable.makeTradableDTO());
                    return cancelledTradable;
                }
            }
        }
        return null;
    }

    public Price topOfBookPrice() {
        if (bookEntries.isEmpty()) {
            return null;
        }
        if (side == BookSide.BUY) { //top of book for BUY is the last key
            return bookEntries.lastKey();
        } else if (side == BookSide.SELL){ //top of book for SELL is first keu
            return bookEntries.firstKey();
        }
        return null;
    }

    public int topOfBookVolume() {
        if (bookEntries.isEmpty()) {
            return 0;
        }

        int totalVolume = 0;
        if (side == BookSide.BUY) { // Get the highest price and iterate through the values to get the total volume
            ArrayList<Tradable> highestPrice = bookEntries.get(bookEntries.lastKey());
            for (Tradable tradable : highestPrice) {
                totalVolume += tradable.getRemainingVolume();
            }
        } else if (side == BookSide.SELL) {
            ArrayList<Tradable> lowestPrice = bookEntries.get(bookEntries.firstKey());
            for (Tradable tradable : lowestPrice) {
                totalVolume += tradable.getRemainingVolume();
            }
        }
        return totalVolume;
    }




    public void tradeOut(Price price, int volToTrade) throws InvalidPriceException, DataValidationException {
        Price top = topOfBookPrice();
        if (top == null) {
            return;
        }
        if (top.greaterThan(price)) {
            return;
        }

        ArrayList<Tradable> atPrice = bookEntries.get(top); //tradables at top price
        int totalVolAtPrice = 0;

        for (Tradable tradable : atPrice) { //getting the total volume of tradables at top price
            totalVolAtPrice += tradable.getRemainingVolume();
        }

        if (volToTrade >= totalVolAtPrice) { //case we fill the order completely

            for (Tradable tradable : atPrice) {
                int rv = tradable.getRemainingVolume();
                tradable.setFilledVolume(tradable.getOriginalVolume());
                tradable.setRemainingVolume(0);
                System.out.println("FULL FILL: (" + tradable.getSide() + " " + rv + ") " + tradable);
                UserManager.getInstance().updateTradable(tradable.getUser(), tradable.makeTradableDTO());
            }
            bookEntries.remove(topOfBookPrice()); //removing arraylist from top price if empty
        } else { //case we partially fill the order
            int remainder = volToTrade;
            for (Tradable tradable : atPrice) {
                double ratio = (double) tradable.getRemainingVolume() / totalVolAtPrice;
                int toTrade = (int) Math.ceil(volToTrade * ratio);
                toTrade = Math.min(remainder, toTrade);
                tradable.setFilledVolume(tradable.getFilledVolume() + toTrade);
                tradable.setRemainingVolume(tradable.getRemainingVolume() - toTrade);
                System.out.println("PARTIAL FILL: (" + tradable.getSide() + " " + toTrade + ") " + tradable);
                remainder -= toTrade;
                UserManager.getInstance().updateTradable(tradable.getUser(), tradable.makeTradableDTO());
            }
        }
    }





    @Override
    public String toString() {
        if (bookEntries.isEmpty()) {
            return "\t<Empty>";
        }

        StringBuilder sb = new StringBuilder();
        if (side == BookSide.BUY) {
            // Iterate in descending order for BUY side
            for (Map.Entry<Price, ArrayList<Tradable>> entry : bookEntries.descendingMap().entrySet()) {
                Price price = entry.getKey();
                ArrayList<Tradable> tradables = entry.getValue();

                sb.append("\t").append(price).append(":\n");
                for (Tradable tradable : tradables) {
                    sb.append("\t\t").append(tradable.toString()).append("\n");
                }
            }
        } else {
            // Iterate in ascending order for SELL side
            for (Map.Entry<Price, ArrayList<Tradable>> entry : bookEntries.entrySet()) {
                Price price = entry.getKey();
                ArrayList<Tradable> tradables = entry.getValue();

                sb.append("\t").append(price).append(":\n");
                for (Tradable tradable : tradables) {
                    sb.append("\t\t").append(tradable.toString()).append("\n");
                }
            }
        }
        return sb.toString();
    }




}
