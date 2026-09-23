package Book;
import Exceptions.*;
import Tradable.*;

import java.util.*;

public final class ProductManager {
    private static ProductManager instance;
    HashMap<String, ProductBook> productBooks = new HashMap<>();

    private ProductManager() {}

    public static ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public void addProduct(String symbol) throws DataValidationException {
        if (symbol == null) {
            throw new DataValidationException("symbol must not be null");
        }
        if (!symbol.matches("[A-Z0-9.]{1,5}")) {
            throw new DataValidationException("Symbol must adhere to naming conventions");
        }
        ProductBook productBookObj = new ProductBook(symbol);
        productBooks.put(symbol, productBookObj);
    }

    public ProductBook getProductBook(String symbol) throws DataValidationException {
        if (!(productBooks.containsKey(symbol))) {
            throw new DataValidationException("symbol must be in productBooks");
        }
        return productBooks.get(symbol);
    }

    public String getRandomProduct() throws DataValidationException {
        if (productBooks.isEmpty()) {
            throw new DataValidationException("No items in productBooks");
        }
        Random generator = new Random(); //random number generator obj
        Object[] products = productBooks.keySet().toArray(); //get product keys and convert to array
        return (String) products[generator.nextInt(products.length)]; //access array at random index (within array indices)
    }

    public TradableDTO addTradable(Tradable o) throws DataValidationException, InvalidPriceException {
        if (o == null) {
            throw new DataValidationException("Tradable cannot be null");
        }
        ProductBook book = productBooks.get(o.getProduct()); //get correct ProductBook
        TradableDTO productBookDTO = book.add(o); //add tradable to ProductBook
        UserManager userManager = UserManager.getInstance();
        userManager.updateTradable(o.getUser(), o.makeTradableDTO());
        return productBookDTO;
    }

    public TradableDTO[] addQuote(Quote q) throws DataValidationException, InvalidPriceException {
        if (q == null) {
            throw new DataValidationException("Quote cannot be null");
        }
        ProductBook book = productBooks.get(q.getSymbol());
        book.removeQuotesForUser(q.getUser());
        TradableDTO buySideDTO = addTradable(q.getQuoteSide(BookSide.BUY));
        TradableDTO sellSideDTO = addTradable(q.getQuoteSide(BookSide.SELL));
        return new TradableDTO[]{buySideDTO, sellSideDTO};
    }

    public TradableDTO cancel(TradableDTO o) throws DataValidationException, InvalidPriceException {
        if (o == null) {
            throw new DataValidationException("Tradable must not be null");
        }
        ProductBook book = productBooks.get(o.product());
        TradableDTO cancelledDTO = book.cancel(o.side(), o.tradableId());
        if (cancelledDTO == null) {
            System.out.println("Failed to cancel tradable");
            return null;
        } else {
            return cancelledDTO;
        }
    }

    public TradableDTO[] cancelQuote(String symbol, String user) throws DataValidationException, InvalidPriceException {
        if (user == null) {
            throw new DataValidationException("user cannot be null");
        }
        if (!productBooks.containsKey(symbol)) {
            throw new DataValidationException("Product Book does not contain key");
        }
        ProductBook book = productBooks.get(symbol);
        return book.removeQuotesForUser(user);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ProductBook> entry : productBooks.entrySet()) {
            sb.append(entry.getValue().toString()).append("...\n");
        }
        return sb.toString();
    }
}
