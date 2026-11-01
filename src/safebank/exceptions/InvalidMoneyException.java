package safebank.exceptions;

public class InvalidMoneyException extends RuntimeException {
    public InvalidMoneyException(String message) {
        super(message);
    }
}
