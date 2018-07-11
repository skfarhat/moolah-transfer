package unit;

import moolah.exceptions.AccountBalanceException;
import moolah.exceptions.AccountException;
import moolah.exceptions.TransferException;
import org.junit.Test;

/**
 * Unit test class that tests the {@package exceptions} package
 */
public class ExceptionsTest {


    // -----------------
    // TransferException
    // -----------------

    @Test (expected = TransferException.class)
    public void testTransferExceptionWithoutMessage() {
        TransferException exc = new TransferException();
        throw exc;
    }

    @Test (expected = TransferException.class)
    public void testTransferExceptionWithMessage() {
        TransferException exc = new TransferException("Exception message from ExceptionTest class.");
        throw exc;
    }

    // ----------------
    // AccountException
    // ----------------

    @Test (expected = AccountException.class)
    public void testAccountExceptionWithoutMessage() {
        AccountException exc = new AccountException();
        throw exc;
    }

    @Test (expected = AccountException.class)
    public void testAccountExceptionWithMessage() {
        AccountException exc = new AccountException("Exception message from ExceptionTest class.");
        throw exc;
    }

    // -----------------------
    // AccountBalanceException
    // -----------------------

    @Test (expected = AccountBalanceException.class)
    public void testAccountBalanceExceptionWithoutMessage() {
        AccountBalanceException exc = new AccountBalanceException();
        throw exc;
    }

    @Test (expected = AccountException.class)
    public void testAccountBalanceExceptionnWithMessage() {
        AccountBalanceException exc = new AccountBalanceException("Exception message from ExceptionTest class.");
        throw exc;
    }
}
