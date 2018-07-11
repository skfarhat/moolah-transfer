package moolah.exceptions;

/**
 * parent class for all Exceptions involving accounts
 */
public class AccountException extends RuntimeException {
    public AccountException() {
    }

    public AccountException(String message) {
        super(message);
    }
}
