package Exceptions;

import java.security.*;

public class InvalidPriceException extends Exception{
    public InvalidPriceException(String message) {
        super(message);
    }
}
