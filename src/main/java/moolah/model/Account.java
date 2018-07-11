package moolah.model;

import moolah.exceptions.AccountBalanceException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


// NOTE: why choose withdraw(), deposit() vs addAmount()?
// first they are more readable.
// Second, they are stricter in checking that their inputs are correct: they both expect a positive amount, meaning if
// ever they receive a negative amount an exception will be thrown and the developer alerted. Whereas for addAmount(),
// both positive and negative are valid and have totally different effects.

/**
 * POJO Account object
 */
@XmlRootElement
public class Account {

    /**
     * account's unique identifier
     */
    private UUID id;

    /**
     * name of the account
     */
    private String name;

    /**
     * name of the owner of the account
     */
    private String owner;

    /**
     * balance of the account
     *
     * Within Account, this value can be made negative.
     *
     * note: no currency is in use at this point
     */
    private Double balance = 0.0;

    /**
     * list of transfers coming in or going out of this account
     */
    private List<Transfer> transfers = new ArrayList<>();

    /**
     * default constructor needed by Jersey
     */
    public Account() { }

    public UUID getId() {
        return id;
    }

    @GET @Path("name")
    public String getName() {
        return name;
    }

    @GET @Path("owner")
    public String getOwner() {
        return owner;
    }

    @GET @Path("balance")
    public Double getBalance() {
        return balance;
    }

    @GET @Path("transfers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Transfer> getTransfers() {
        return transfers;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    /**
     * Increments the balance by the provided {@code amount}
     *
     * @param amount to deposit, must be positive
     */
    public void deposit(Double amount) {
        if (amount < 0)
            throw new AccountBalanceException("Amount to deposit cannot be negative.");
        balance += amount;
    }

    /**
     * Decrements the balance by the provided {@code amount}
     *
     * The caller of this method is responsible for verifying if the amount to be withdrawn is greater than the current
     * balance of the account and deciding if that should be allowed or not.
     *
     * @param amount to withdraw, must be positive
     */
    public void withdraw(Double amount) {
        if (amount < 0)
            throw new AccountBalanceException("Amount to withdraw cannot be negative.");
        balance -= amount;
    }

    /**
     * add Transfer {@param transfer} to the list of transfers
     * @param transfer transfer to be added
     */
    public void addTransfer(Transfer transfer) {
        transfers.add(transfer);
    }

    /**
     * remove Transfer {@param transfer} from the list of transfers
     * @param transfer transfer to be removed
     */
    public void removeTransfer(Transfer transfer) {
        transfers.remove(transfer);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || !(obj instanceof Account))
            return false;

        Account o = (Account) obj;
        return getId().equals(o.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
