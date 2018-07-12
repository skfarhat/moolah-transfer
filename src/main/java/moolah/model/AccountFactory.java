package moolah.model;

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
}
