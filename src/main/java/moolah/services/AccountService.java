package moolah.services;

import moolah.exceptions.AccountBalanceException;
import moolah.exceptions.TransferException;
import moolah.exceptions.web.BadRequestException;
import moolah.exceptions.web.NotFoundException;
import moolah.model.Account;
import moolah.model.AccountFactory;
import moolah.model.Transfer;
import moolah.model.TransferManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

import static moolah.services.AccountService.ACCOUNTS_ROOT;

/**
 *
 */
@Path(ACCOUNTS_ROOT)
public class AccountService {

    public static final String ACCOUNTS_ROOT = "/accounts";
    public static final String ACCOUNTS_ALL = "/";
    public static final String ACCOUNTS_SINGLE_ID = "/{id}";
    public static final String ACCOUNTS_ACCOUNT_PARAM = "/{id}/p";
    public static final String ACCOUNTS_CREATE = "/";
    public static final String ACCOUNTS_UPDATE = "/{id}";
    public static final String ACCOUNTS_TRANSFER = "/{fromId}/transfer/{toId}";

    private static Map<UUID, Account> accounts = new HashMap<>();

    /**
     * stores an account in the {@code accounts} HashMap
     *
     * @param acct
     */
    public void addAccount(Account acct) {
        accounts.put(acct.getId(), acct);
    }

    /**
     * remove the account stored in the {@code accounts} HashMap
     *
     * @param acct
     */
    public void removeAccount(Account acct) {
        accounts.remove(acct.getId());
    }

    /**
     * GET /accounts/
     *
     * @return list of all accounts
     */
    @GET
    @Path(ACCOUNTS_ALL)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    /**
     * returns the account associated with {@param id}
     * @param id the Id of the account to be returned
     * @return the Account instance in {@code accounts} that has Id {@param id}
     */
    private Account getAccountPrv(UUID id) {
        Account acct = accounts.get(id);
        if (acct == null) {
            throw new NotFoundException("Account not found");
        }
        return acct;
    }

    /**
     * GET /accounts/{id}
     *
     * @param id of the Account to be returned
     * @return the Account object stored in the {@code accounts} HashMap, null if the account is not stored in the map.
     */
    @GET
    @Path(ACCOUNTS_SINGLE_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("id") UUID id) {
        return getAccountPrv(id);
    }

    /**
     * GET /accounts/{id}
     *
     * @param id of the Account to be returned
     * @return the Account object stored in the {@code accounts} HashMap, null if the account is not stored in the map.
     */
    @Path(ACCOUNTS_ACCOUNT_PARAM)
    public Account getAccountParam(@PathParam("id") UUID id) {
        return getAccountPrv(id);
    }

    /**
     * CREATE
     *
     * POST /accounts/create
     *
     * Consumes a JSON account object that is used in the creation of a new account object.
     * The id field in the provided consumed JSON will be ignored.
     * The JSON representing the created object will be produced in a 201 CREATED Response.
     *
     * @param account
     * @return HTTP Response
     */
    @POST
    @Path(ACCOUNTS_CREATE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(Account account) {
        try {
            Account toAdd = AccountFactory.createAccount(account);
            addAccount(toAdd);
            return Response.created(URI.create(AccountService.ACCOUNTS_ROOT + "/" + toAdd.getId())).entity(toAdd).build();
        }
        catch(AccountBalanceException exc) {
            throw new BadRequestException(exc.getMessage());
        }
    }

    /**
     * UPDATE
     *
     * POST /accounts/{id}
     *
     * @param id the Id of the account to update.
     * @param update Account object whose contents will be used to update existing object
     * @return HTTP Response
     */
    @POST
    @Path(ACCOUNTS_UPDATE)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(@PathParam("id") UUID id, Account update) {
        Account toUpdate = accounts.get(id);
        if (update.getId() != null && !update.getId().equals(id)) {
            throw new BadRequestException("PathParam id and JSON id do not match");
        }
        if (toUpdate == null) {
            throw new NotFoundException(String.format("Could not find account with id '%s' to update", id));
        }
        toUpdate.setOwner(update.getOwner());
        toUpdate.setName(update.getName());
        return Response.ok().entity(toUpdate).build();
    }

    /**
     * DELETE
     *
     * DELETE /accounts/{id}
     *
     * Delete the account associated with {id}
     *
     * @param id the Id of the account to be deleted.
     *
     * @return the Account that was deleted.
     */
    @DELETE
    @Path(ACCOUNTS_SINGLE_ID)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(@PathParam("id") UUID id) {
        Account toDelete = accounts.get(id);
        if (toDelete == null) {
            throw new NotFoundException(String.format("Could not find account with id '%s' to delete", id));
        }
        removeAccount(toDelete);
        return Response.ok().entity(toDelete).build();
    }

    /**
     * TRANSFER
     *
     * POST /accounts/{fromId}/transfer/{toId}
     *
     * Issues a money transfer from {@param fromId} to {@param toId}. The transfer details are fetched from the Transfer
     * object {@param transferRequest}
     *
     * @param fromId the Id of the Account to transfer from
     * @param toId the Id of the Account to transfer to
     * @param transferRequest a Transfer object encompassing the requested transfer details.
     *                        Note that this will be different from the actual Transfer object that is generated.
     * @throws TransferException if the transfer fails
     *
     * @return the Transfer object generated if the Transfer was successful.
     */
    @POST
    @Path(ACCOUNTS_TRANSFER)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Transfer transfer(@PathParam("fromId") UUID fromId, @PathParam("toId") UUID toId, Transfer transferRequest) {
        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);

        if (from == null) {
            throw new NotFoundException("From account not found.");
        }
        if (to == null) {
            throw new NotFoundException("To account not found.");
        }

        try {
            return TransferManager.doTransfer(from, to, transferRequest.getAmount(), transferRequest.getName());
        }
        catch (TransferException exc) {
            throw new BadRequestException(exc.getMessage());
        }
    }

}
