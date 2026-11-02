package safebank.exceptions;

public class AccountNumberNotFound extends RuntimeException {
    public AccountNumberNotFound(String message) {
        super(message);
    }
}
