# Stock Exchange System
 
A plain-Java simulation of a stock exchange matching engine. It models an order book that accepts **limit orders** and two‑sided **quotes**, matches them against resting liquidity on a price/time basis, and publishes top‑of‑book market data to subscribed users — all built from scratch with no external frameworks.
 
## Features
 
- **Order book matching** — buy/sell orders are matched against resting orders and quotes at the best available price, with partial fills supported.
- **Two-sided quotes** — a single user can post a bid and an offer at once; posting a new quote automatically replaces that user's previous one.
- **Order & quote cancellation** — cancel a specific order by id, or cancel all of a user's resting orders/quotes for a symbol.
- **Current market data (top of book)** — an observer/publisher pattern (`CurrentMarketTracker` → `CurrentMarketPublisher`) pushes best bid/offer updates to every user subscribed to a symbol.
- **Multi-product support** — `ProductManager` manages an independent order book per symbol (e.g. `WMT`, `TGT`).
- **User accounts** — each user tracks their own open orders/quotes (via `TradableDTO` snapshots) and the current markets they're subscribed to.
- **Cent-precision pricing** — `Price` stores values as integer cents and supports parsing from strings (e.g. `"159.90"`), avoiding floating-point rounding errors.
- **Input validation** — custom checked exceptions guard against invalid prices, volumes, book sides, and unknown users/symbols.
## Architecture
 
The code lives under `src/` with no package-management build tool (no Maven/Gradle) — it's set up as a plain IntelliJ IDEA module (`stock_exchange_system.iml`) with `src/` as the source root.
 
```
src/
├── Main.java              # Demo entry point that drives the whole system end-to-end
├── Book/                   # Order book and user domain logic
│   ├── ProductBook.java        # A single symbol's combined buy/sell book; matching entry point
│   ├── ProductBookSide.java    # One side (BUY or SELL) of a ProductBook, keyed by price
│   ├── ProductManager.java     # Singleton registry of all ProductBooks, keyed by symbol
│   ├── Quote.java               # A two-sided (buy + sell) quote from a single user
│   ├── User.java                 # A market participant; tracks their tradables and market data
│   └── UserManager.java          # Singleton registry of all Users, keyed by user id
├── Market/                 # Top-of-book market data distribution
│   ├── CurrentMarketTracker.java     # Computes and logs the current best bid/offer for a symbol
│   ├── CurrentMarketPublisher.java   # Singleton pub/sub hub; routes updates to subscribers
│   ├── CurrentMarketSide.java        # Value object: a price + volume on one side of the market
│   └── CurrentMarketObserver.java    # Interface implemented by subscribers (e.g. User)
├── Price/                  # Immutable, cached price value type
│   ├── Price.java                 # Cent-based, comparable price with arithmetic helpers
│   └── PriceFactory.java          # Factory that interns/reuses Price instances
├── Tradable/               # Order-book line-item abstraction
│   ├── Tradable.java              # Interface implemented by anything that can sit in a book
│   ├── Order.java                 # A standard one-sided limit order
│   ├── QuoteSide.java             # One side of a Quote, adapted to the Tradable interface
│   ├── TradableDTO.java           # Immutable snapshot of a Tradable's state, for external use
│   └── BookSide.java              # BUY / SELL enum
└── Exceptions/             # Checked exceptions for domain validation
    ├── DataValidationException.java
    ├── InvalidBookSideException.java
    ├── InvalidPriceException.java
    ├── InvalidVolumeException.java
    └── UnknownUserException.java
```
 
### Design patterns in play
 
- **Singleton** — `ProductManager`, `UserManager`, and `CurrentMarketPublisher` are all single, globally-shared instances.
- **Observer / Publisher-Subscriber** — `User` implements `CurrentMarketObserver`; `CurrentMarketPublisher` notifies every user subscribed to a symbol whenever the top of book changes.
- **Factory + flyweight** — `PriceFactory` returns a shared `Price` instance for a given cent value instead of constructing duplicates.
- **DTO** — `TradableDTO` is an immutable snapshot handed out to `User` objects so that internal book state can't be mutated from outside.
## Requirements
 
- Java Development Kit (JDK) 8 or later (no external dependencies/build tool required).
## Getting Started
 
### Run from the command line
 
```bash
git clone https://github.com/tenochca/stock-exchange-system.git
cd stock-exchange-system
 
# Compile
javac -d out $(find src -name "*.java")
 
# Run
java -cp out Main
```
 
### Run from IntelliJ IDEA
 
The repo ships with a `stock_exchange_system.iml` module file, so it can be opened directly in IntelliJ IDEA:
 
1. **File → Open...** and select the project folder.
2. Let IntelliJ index the existing module (`src` is already configured as the source root).
3. Right-click `src/Main.java` and choose **Run 'Main.main()'**.
## What the demo does
 
`Main.java` is a self-contained walkthrough of the whole system rather than a long-running server. It:
 
1. Registers two products (`WMT`, `TGT`) and five users (`ANA`, `BOB`, `COD`, `DIG`, `EST`).
2. Subscribes several users to current-market updates for those symbols.
3. Builds up both sides of the `TGT` book with quotes, then submits orders that trade against the book, partially and fully.
4. Amends and cancels quotes/orders, printing the book, user states, and current market data after each step so you can follow the matching engine's behavior in the console output.
## Project Status
 
This is a learning/demo project focused on core matching-engine mechanics (order books, quotes, matching, market data) rather than a production trading system — there's no persistence, networking, or UI layer.
 
