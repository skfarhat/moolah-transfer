package moolah.model;

import moolah.exceptions.TransferException;

import java.util.Date;
import java.util.UUID;

public class TransferManager {

    /**
     * throws TransferException if:
     *
     *  - {@code from} and {@code to} account are the same
     *  - {@code amount} value is not positive
     *  - {@code from} balance is lower than amount
     *
     * otherwise returns.
     *
     * @param from account to transfer money from
     * @param to account to transfer money to
     * @param amount amount to be transferred between accounts
     * @throws TransferException
     */
    public static void checkTransfer(Account from, Account to, Double amount) throws TransferException {
        if (amount <= 0) {
            throw new TransferException("Invalid amount. Transfer amount must be strictly positive.");
        }
        if (from.equals(to)) {
            throw new TransferException("Invalid transfer. 'to' and 'from' accounts are the same.");
        }
        if (from.getBalance() < amount) {
            throw new TransferException(String.format("Account %s does not contain enough funds.", from));
        }
    }

    /**
     *
     * @param from
     * @param to
     * @param amount
     * @param name
     * @return
     */
    public static Transfer doTransfer(Account from, Account to, Double amount, String name) {
        checkTransfer(from, to, amount);

        // create and set Transfer object
        Transfer transfer = new Transfer();
        transfer.setId(UUID.randomUUID());
        transfer.setFrom(from);
        transfer.setTo(to);
        transfer.setDate(new Date());
        transfer.setAmount(amount);
        transfer.setName(name);

        // change amounts
        from.withdraw(amount);
        to.deposit(amount);

        // add the transfer object to both 'from' and 'to' accounts
        from.addTransfer(transfer);
        to.addTransfer(transfer);

        return transfer;
    }
}
