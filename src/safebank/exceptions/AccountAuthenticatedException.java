package safebank.exceptions;

public class AccountAuthenticatedException extends RuntimeException {
    public AccountAuthenticatedException(String message) {
        super(message);
    }
}
