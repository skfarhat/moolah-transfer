package moolah.model;

import moolah.exceptions.AccountException;

import java.util.UUID;

public class AccountFactory {

    public static Account createAccount(Account toCopy) {
        return createAccount(toCopy.getName(), toCopy.getOwner(), toCopy.getBalance());
    }

    public static Account createAccount(String name, String owner, Double balance) {
        Account act = new Account();
        act.setOwner(owner);
        act.setName(name);
        act.deposit(balance);
        act.setId(UUID.randomUUID());
        return act;
    }

    /**
     * modify the fields in {@code toUpdate} to match those of {@code update}.
     *
     * Note: ID and balance are ignored during the update.
     *
     * @param toUpdate
     * @param update
     * @return the updated object
     */
    public static Account updateAccount(Account toUpdate, Account update) {
        if (toUpdate == null || update == null)
            throw new AccountException("Account parameters passed to updateAccount() cannot be null.");
        toUpdate.setOwner(update.getOwner());
        toUpdate.setName(update.getName());
        return toUpdate;
    }
}
