package moolah.exceptions;

/**
 * Thrown when an illegal operation when an illegal operation on an Account's balance is attempted.
 */
public class AccountBalanceException extends AccountException {

    public AccountBalanceException() {
        super();
    }

    public AccountBalanceException(String message) {
        super(message);
    }
}
