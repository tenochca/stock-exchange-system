package Price;

import Exceptions.*;

import java.util.*;

public abstract class PriceFactory {

    private static ArrayList<Price> currentPriceObjects = new ArrayList<>();

    public static Price makePrice(int value) {
        if (!currentPriceObjects.contains(new Price(value))) { //if there is not existing price obj create and return a new one
            Price newPrice = new Price(value);
            currentPriceObjects.add(newPrice);
            return newPrice;
        } else {
            int idx = currentPriceObjects.indexOf(new Price(value));
            return currentPriceObjects.get(idx);
        }
    }


    public static Price makePrice(String stringValueIn) throws InvalidPriceException {
        String cleanedString = cleanString(stringValueIn); //clean the string to work with
        boolean isNegative = cleanedString.contains("-"); //check if the value should be negative
        cleanedString = cleanedString.replaceAll("-", ""); //once we know its negative we're done need the negative sign
        int periodIdx = -1; //set period index to arbitrary -1 | will update

        for (int i = 0; i < cleanedString.length(); i++) { //find period index
            if (cleanedString.charAt(i) == '.') {
                periodIdx = i;
                break;
            }
        }

        int totalCents = calculateTotalCents(cleanedString, periodIdx); //calc total cents bases on the cleaned string and index of interest

        if (isNegative) { //if the number was deemed negative reinsert the negative sign
            totalCents = -totalCents;
        }

        if (!currentPriceObjects.contains(makePrice(totalCents))) { //if there is not existing price obj create and return a new one
            Price newPrice = makePrice(totalCents);
            currentPriceObjects.add(newPrice);
            return newPrice;
        } else {
            int idx = currentPriceObjects.indexOf(makePrice(totalCents));
            return currentPriceObjects.get(idx);
        }
    }

    private static String cleanString(String stringValueIn) {
        return stringValueIn.replaceAll("[$,]", "");
    }


    private static int calculateTotalCents(String cleanedString, int periodIdx) throws InvalidPriceException {
        //check simple cases first

        if (periodIdx == -1) { //period is not present
            return Integer.parseInt(cleanedString) * 100;
        } else if (periodIdx == 0) { //period is at the beginning
            return Integer.parseInt(cleanedString.substring(periodIdx + 1));
        } else if (periodIdx == cleanedString.length() - 1) { //period is at the end
            return Integer.parseInt(cleanedString.substring(0, periodIdx)) * 100;
        } else { //otherwise if it is somewhere in the middle
            String dollarsSub = cleanedString.substring(0, periodIdx);
            String centsSub = cleanedString.substring(periodIdx + 1);

            if (centsSub.isEmpty() || centsSub.length() == 2) { //check ensure we only have 2 digits or none after the period
                int dollars = Integer.parseInt(dollarsSub);
                int cents = Integer.parseInt(centsSub);
                return (dollars * 100) + cents;
            } else {
                throw new InvalidPriceException("Cannot have cents above or below 2 digits");
            }
        }
    }
}
