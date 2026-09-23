package Price;

import Exceptions.*;

import java.util.*;

public class Price implements Comparable<Price> {

    private final int cents;

    Price(int cents) {
        this.cents = cents;
    }

    public int getCents() {
        return cents;
    }

    public boolean isNegative() {
        if (cents < 0) {
            return true;
        } else {
            return false;
        }
    }

    public Price add(Price p) throws InvalidPriceException {
        if (p == null) {
            throw new InvalidPriceException("Cannot accept null Price.Price object");
        }
        int sum = cents + p.cents;
        return PriceFactory.makePrice(sum);
    }

    public Price subtract(Price p) throws InvalidPriceException {
        if (p == null) {
            throw new InvalidPriceException("Cannot accept null price object");
        }
        int dif = cents - p.cents;
        return PriceFactory.makePrice(dif);
    }

    public Price multiply(int n) {
        int product = cents * n;
        return PriceFactory.makePrice(product);
    }

    public boolean greaterOrEqual(Price p) throws InvalidPriceException {
        if (p == null) {
            throw new InvalidPriceException("Cannot accept null price object");
        }
        return cents >= p.cents;
    }

    public boolean lessOrEqual(Price p) throws InvalidPriceException {
        if (p == null) {
            throw new InvalidPriceException("Cannot accept null price object");
        }
        return cents <= p.cents;
    }

    public boolean greaterThan(Price p) throws InvalidPriceException {
        if (p == null) {
            throw new InvalidPriceException("Cannot accept null price object");
        }
        return cents > p.cents;
    }

    public boolean lessThan(Price p) throws InvalidPriceException {
        if (p == null) {
            throw new InvalidPriceException("Cannot accept null price object");
        }
        return cents < p.cents;
    }

    @Override
    public int compareTo(Price p) {
        if (p == null) {
            System.out.println("Cannot compare null Price.Price objects");
            return -1;
        }
        return cents - p.cents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return cents == price.cents;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cents);
    }

    @Override
    public String toString() {
        int dollars = cents / 100;
        int remainder = Math.abs(cents % 100);

        if (dollars == 0 && isNegative()) {
            return String.format("$-%,d.%02d", dollars, remainder);
        } else {
            return String.format("$%,d.%02d", dollars, remainder);
        }
    }
}


