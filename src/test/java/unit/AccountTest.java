package unit;

import moolah.exceptions.AccountBalanceException;
import moolah.model.Account;
import moolah.model.AccountFactory;
import moolah.model.Transfer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

public class AccountTest {

    private static final String SAMPLE_OWNER_NAME = "John";

    private static final String SAMPLE_ACCOUNT_NAME = "Checking";

    /**
     * @param balance
     * @return a sample account with the {@code balance} provided.
     */
    public static Account getAccount(Double balance) {
        return AccountFactory.createAccount(SAMPLE_ACCOUNT_NAME, SAMPLE_OWNER_NAME, balance);
    }

    @Test
    public void testDepositPositiveAmount() {
        final Double INITIAL = 0.0;
        final Double TO_DEPOSIT = 1000.0;
        Double expected = INITIAL + TO_DEPOSIT;
        Account act = getAccount(INITIAL);
        act.deposit(TO_DEPOSIT);

        Assert.assertEquals(expected, act.getBalance());
    }

    @Test(expected = AccountBalanceException.class)
    public void testDepositNegativeAmountThrowsException() {
        final Double INITIAL = 0.0;
        final Double TO_DEPOSIT = -1000.0;
        Account act = getAccount(INITIAL);
        act.deposit(TO_DEPOSIT);
    }

    @Test
    public void testAddAmountPositiveValue() {
        final Double INITIAL = 0.0;
        Account act = getAccount(INITIAL);

        final Double TO_ADD = 1000.0;
        act.deposit(TO_ADD);
        Assert.assertEquals(TO_ADD, act.getBalance());
    }

    @Test(expected = AccountBalanceException.class)
    public void testDepositNegativeAmountThowsException() {
        final Double INITIAL = 1000.0;
        final Double NEGATIVE_AMOUNT = -INITIAL;

        // setup the account with some amount
        Account account = new Account();
        account.deposit(INITIAL);

        // deposit a negative amount, exception should be thrown
        account.deposit(NEGATIVE_AMOUNT);
    }

    @Test(expected = AccountBalanceException.class)
    public void testWithdrawNegativeAmountThrowsException() {
        final Double INITIAL = 1000.0;
        final Double NEGATIVE_AMOUNT = -INITIAL;

        Account account = new Account();
        account.deposit(INITIAL);

        // should throw exception
        account.withdraw(NEGATIVE_AMOUNT);
    }


    @Test
    public void testEqualsTrueWhenSameAccount() {
        Account accountA = new Account();
        Account accountB = accountA;
        Assert.assertTrue(accountA.equals(accountB));
    }

    @Test
    public void testEqualsFalseWhenNull() {
        Account accountA = new Account();
        Account accountB = null;
        Assert.assertFalse(accountA.equals(accountB));
    }

    @Test
    public void testEqualsFalseWhenDifferentObjectType() {
        Account accountA = new Account();
        Object objB = new Object();
        Assert.assertFalse(accountA.equals(objB));
    }

    @Test
    public void testAddTransferIn() {
        Account account = new Account();
        final UUID transferId = UUID.randomUUID();
        Transfer transfer = new Transfer();
        transfer.setId(transferId);

        account.addTransfer(transfer);

        List<Transfer> transfers = account.getTransfers();

        // Check that the Transfer was added
        Assert.assertEquals(1, transfers.size());
        Assert.assertEquals(transfer, transfers.get(0));
    }

    /**
     * test starts out like {@code testAddTransferIn} then removes the Transfer and validates removal succeeded.
     */
    @Test
    public void testRemoveTransfer() {
        Account account = new Account();
        final UUID transferId = UUID.randomUUID();
        Transfer transfer = new Transfer();
        transfer.setId(transferId);

        account.addTransfer(transfer);

        List<Transfer> transfers = account.getTransfers();

        // Check that the Transfer was added
        Assert.assertEquals(1, transfers.size());
        Assert.assertEquals(transfer, transfers.get(0));

        // Remove Transfer and check that it worked
        account.removeTransfer(transfer);
        transfers = account.getTransfers();

        // Check that the Transfer was added
        Assert.assertEquals(0, transfers.size());
    }

}
