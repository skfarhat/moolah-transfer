package moolah.exceptions;

/**
 * Thrown for errors relating transfers
 */
public class TransferException extends RuntimeException {
    public TransferException() { }

    public TransferException(String message) {
        super(message);
    }
}
