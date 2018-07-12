package unit;

import moolah.exceptions.TransferException;
import moolah.model.Account;
import moolah.model.Transfer;
import moolah.model.TransferManager;
import org.junit.Assert;
import org.junit.Test;

public class TransferTest {

    @Test
    public void testSimpleTransferBetweenAccounts() {
        final Double INITIAL = 10000.0;
        final String NAME = "TRANSFER-1";

        Account from = AccountTest.getAccount(INITIAL);
        Account to = AccountTest.getAccount(INITIAL);

        // setup initial amounts in each account
        final Double TRANSFER_AMOUNT = 5000.0;
        Double EXPECTED_FROM = (INITIAL - TRANSFER_AMOUNT);
        Double EXPECTED_TO = (INITIAL + TRANSFER_AMOUNT);

        // Make  the transfer
        Transfer transfer = TransferManager.doTransfer(from, to, TRANSFER_AMOUNT, NAME);

        // Check
        Assert.assertEquals(EXPECTED_FROM, from.getBalance());
        Assert.assertEquals(EXPECTED_TO, to.getBalance());
        Assert.assertEquals(NAME, transfer.getName());
    }

    /**
     * should fail because 'to' and 'from' account are the same
     */
    @Test(expected = TransferException.class)
    public void testExceptionThrownWhenToAndFromAccountsAreTheSame() {
        final Double INITIAL = 1000.0;
        final Double TRANSFER_AMOUNT = 5000.0;

        Account from = AccountTest.getAccount(INITIAL);
        Account to = from;

        // Make  the transfer
        TransferManager.doTransfer(from, to, TRANSFER_AMOUNT, null);
    }
    /**
     * should fail because not enough amount in balance
     */
    @Test(expected = TransferException.class)
    public void testExceptionThrownWhenNotEnoughInFromBalance() {
        final Double INITIAL = 1000.0;
        final Double TRANSFER_AMOUNT = 5000.0;
        Account from = AccountTest.getAccount(INITIAL);
        Account to = AccountTest.getAccount(INITIAL);

        // Make  the transfer
        TransferManager.doTransfer(from, to, TRANSFER_AMOUNT, null);
    }

    /**
     * should fail because transfers of negative amounts shouldn't be allowed
     */
    @Test(expected = TransferException.class)
    public void testExceptionThrownWhenAmountIsNegative() {
        final Double INITIAL = 10000.0;
        final Double TRANSFER_AMOUNT = -1000.0;

        Account from = AccountTest.getAccount(INITIAL);
        Account to = AccountTest.getAccount(INITIAL);

        // Make  the transfer
        TransferManager.doTransfer(from, to, TRANSFER_AMOUNT, null);
    }

    @Test
    public void testEqualsWhenAgainstNull() {
        Transfer t = new Transfer();
        Assert.assertFalse(t.equals(null));
    }

    @Test
    public void testEqualsWhenAgainstSelf() {
        Transfer t = new Transfer();
        Assert.assertTrue(t.equals(t));
    }

    @Test
    public void testEqualsWhenAgainstNonTransferObject() {
        Transfer t = new Transfer();
        Object o = new Object();
        Assert.assertFalse(t.equals(o));
    }

}
