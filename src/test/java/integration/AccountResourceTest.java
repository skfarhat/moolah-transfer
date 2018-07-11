package integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import moolah.model.Account;
import moolah.model.AccountFactory;
import moolah.model.Transfer;
import moolah.resources.AccountResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * Integration test class for AccountResource
 */
public class AccountResourceTest extends JerseyTest {

    /**
     * test accounts used in this class
     *
     * The Accounts will be added to the {@code accountResource} object statically.
     */
    private static ArrayList<Account> testAccounts = new ArrayList<Account>(){{
        add(AccountFactory.createAccount("Checking", "John", 2000.0));
        add(AccountFactory.createAccount("Saving", "Beatrix", 4000.0));
    }};

    private static AccountResource accountResource;

    // Initialise test clients in static block (do it only once)
    static {
        /**
         * AccountResource object instantiated statically and initialised with testAccounts that will be used in
         * test methods in this class.
         */
        accountResource = new AccountResource();
        for (Account a : testAccounts)
            accountResource.addAccount(a);
    }

    /**
     * GET /accounts/{id}/xml
     *
     * Check that /accounts/{id}/xml returns the Account object when there is such an object.
     */
    @Test
    public void testGETSingleAccountFromXML() {
        Account testAccount = testAccounts.get(0);
        final String URI = String.format("%s/%s/xml", AccountResource.ACCOUNTS_ROOT, testAccount.getId().toString());
        Account fetchedAccount = target(URI).request().get(Account.class);
        Assert.assertEquals(testAccount, fetchedAccount);
    }

    /**
     * GET /accounts/{id}
     *
     * Check that /accounts/{id} returns the Account object when there is such an object.
     */
    @Test
    public void testGETSingleAccount() {
        Account testAccount = testAccounts.get(0);
        final String URI = AccountResource.ACCOUNTS_ROOT + "/" + testAccount.getId().toString();
        Account fetchedAccount = target(URI).request().get(Account.class);
        Assert.assertEquals(testAccount, fetchedAccount);
    }

    /**
     * GET /accounts/{id}
     *
     * Check that /accounts/{id} returns a 404 NOT FOUND when the provided ID path does not exist.
     */
    @Test
    public void testGETSingleAccountWhenAbsent() {
        final String NON_EXISTENT_ID = UUID.randomUUID().toString();
        final String URI = AccountResource.ACCOUNTS_ROOT + "/" + NON_EXISTENT_ID;
        Response response = target(URI).request().get();
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * GET /accounts/
     *
     * Check that the method returns all test account objects.
     */
    @Test
    public void testGETAccounts() {

        // Make GET request and get response
        final String URI = AccountResource.ACCOUNTS_ROOT;
        Response response = target(URI).request().get();

        // Check that response status code is 200 OK
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        List<Account> responseList = response.readEntity(new GenericType<List<Account>>(){});

        // Check that the returned list contains at least the amount of elements from testAccounts.
        // The number of returned accounts will depend on other tests and what they have inserted / deleted.
        Assert.assertTrue(testAccounts.size()<= responseList.size());

        // Check that the two lists are effectively equal. Note that the check does not care if the entries were
        // ordered differently.
        // The 'verifier' checks that
        Map<UUID, Boolean> verifier = new HashMap<>();
        for (Account a : responseList) {
            // Check there are no duplicates in the returned list (duplicates = entries with same id)
            Assert.assertFalse(verifier.containsKey(a.getId()));

            // Check that the returned account matches the its testAccount.
            Account testAccount = accountResource.getAccount(a.getId());
            Assert.assertEquals(testAccount, a);
        }
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(AccountResource.class).packages("moolah");
    }

    /**
     * CREATE
     *
     * POST /accounts/
     *
     * Check that account creation works.
     */
    @Test
    public void testCreateAccount() {
        // Prepare for request
        Account accountToCreate = new Account();
        accountToCreate.setName("ISA Account");
        accountToCreate.setOwner("Tobias");
        accountToCreate.deposit(10000.0);
        Entity entityToPost = Entity.entity(accountToCreate, MediaType.APPLICATION_JSON);

        // Make request and get response
        final String URI = AccountResource.ACCOUNTS_ROOT;
        Response response = target(URI).request().post(entityToPost);
        Account accountFromResponse = response.readEntity(Account.class);

        // Manually override the ID in the original account used for creation
        accountToCreate.setId(accountFromResponse.getId());

        // Verify that both accounts are equal
        Assert.assertEquals(accountToCreate, accountFromResponse);

        // Check STATUS code
        Assert.assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        // Since all is working well, add the created account to testAccounts.
        // This is needed because other tests use testAccounts to validate the service's data.
        testAccounts.add(accountToCreate);
    }

    /**
     * CREATE
     *
     * POST /accounts/
     *
     * Check that a "400 Bad Request" is the response from attempting to create an account with a negative balance.
     */
    @Test
    public void testCreateAccountWithNegativeBalance() throws JsonProcessingException {
        // The code will not allow us to create an Account object with a negative amount since an AccountBalanceException
        // will be thrown.
        // So we create one with a positive amount, get the JSON output from that and then negate the balance amount
        // from the JSON string.
        // Then we POST the JSON string.
        Account accountToCreate = new Account();
        accountToCreate.setName("ISA Account");
        accountToCreate.setOwner("Tobias");
        accountToCreate.deposit(10000.0);

        // change the balance in the JsonNode
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.valueToTree(accountToCreate);
        ((ObjectNode) root).remove("balance");
        ((ObjectNode) root).put("balance", -accountToCreate.getBalance());
        accountToCreate = mapper.treeToValue(root, Account.class);

        // Make  request and get response
        final String URI = AccountResource.ACCOUNTS_ROOT;
        Entity toPost = Entity.entity(accountToCreate, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);

        // Check that the response is 400 Bad Request
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    /**
     * UPDATE
     *
     * POST /accounts/{id}
     *
     * Check that name and owner fields can be updated.
     */
    @Test
    public void testUpdateAccountUpdatesForNameAndOwner() {
        // Test-PreRequisite: Ensure we have at least one test account to work with
        Assert.assertTrue(testAccounts.size() > 0);

        // Choose an account to update
        final Account original = testAccounts.get(0);

        // POJO used for the update
        Account updateAccount = new Account();
        final String UPDATED_NAME = original.getName() + "-updated-name";
        final String UPDATED_OWNER = original.getOwner() + "-updated-owner";
        updateAccount.setName(UPDATED_NAME);
        updateAccount.setOwner(UPDATED_OWNER);

        // Make request and get a response
        final String URI = String.format("%s/%s", AccountResource.ACCOUNTS_ROOT, original.getId());
        Entity toPost = Entity.entity(updateAccount, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);
        Account accountFromResponse = response.readEntity(Account.class);

        // Check that the fetched account's name and owner match those used in the update
        Assert.assertEquals(original.getId(), accountFromResponse.getId());
        Assert.assertEquals(UPDATED_NAME, accountFromResponse.getName());
        Assert.assertEquals(UPDATED_NAME, accountFromResponse.getName());
    }

    /**
     * UPDATE
     *
     * POST /accounts/{id}
     */
    @Test
    public void testUpdateAccountWithUnequalIdsReturnsBadRequest() {
        // Test-PreRequisite: Ensure we have at least one test account to work with
        Assert.assertTrue(testAccounts.size() > 0);

        // Choose an account to update
        final Account original = testAccounts.get(0);

        // POJO used for the update
        Account updateAccount = new Account();
        final String UPDATED_NAME = original.getName() + "-updated-name";
        final String UPDATED_OWNER = original.getOwner() + "-updated-owner";
        updateAccount.setName(UPDATED_NAME);
        updateAccount.setOwner(UPDATED_OWNER);
        // set a random UID
        updateAccount.setId(UUID.randomUUID());

        // Make request and get a response
        final String URI = String.format("%s/%s", AccountResource.ACCOUNTS_ROOT, original.getId());
        Entity toPost = Entity.entity(updateAccount, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    /**
     * UPDATE
     *
     * POST /accounts/{id}
     *
     * Check that a 404 Status code is returned when updating an non-existent account.
     */
    @Test
    public void testUpdateAccountWithInvalidIDReturns404NotFound() {

        // We create a new account and try to update it
        Account newAccount = AccountFactory.createAccount("ISA", "Ron", 1000.0);

        // Prepare the POST /accounts/{id} request, with the new ID
        final String URI = String.format("%s/%s", AccountResource.ACCOUNTS_ROOT, newAccount.getId());
        Entity toPost = Entity.entity(newAccount, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);

        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * UPDATE
     *
     * POST /accounts/{id}
     *
     * Check that the balance of an account cannot be changed via POST /accounts/{id}.
     */
    @Test
    public void testUpdateAccountRejectsBalanceUpdates() {
        // Test-PreRequisite: ensure we have at least one test account to work with
        Assert.assertTrue(testAccounts.size() > 0);

        // Choose an account to update
        final Account original = testAccounts.get(0);
        final Double originalBalance = original.getBalance();

        // Copy an account from the test accounts and use it as a Mock object to POST
        Account updateAccount = new Account();
        updateAccount.deposit(3000.0);
        updateAccount.setName(original.getName());
        updateAccount.setOwner(original.getOwner());

        // Prepare a request with the POJO as entity, then get response
        final String URI = String.format("%s/%s", AccountResource.ACCOUNTS_ROOT, original.getId());
        Entity toPost = Entity.entity(updateAccount, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);

        // Check that the status code is OK
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Check that the balance is unchanged
        Account accountFromResponse = response.readEntity(Account.class);
        Assert.assertEquals(originalBalance, accountFromResponse.getBalance());
    }

    /**
     * DELETE
     *
     * DELETE /accounts/{id}
     *
     */
    @Test
    public void testDeleteAccount() {
        // Test PreRequisite
        Assert.assertTrue(testAccounts.size() > 1);

        Account toDelete = testAccounts.get(0);

        final String URI = String.format("%s/%s", AccountResource.ACCOUNTS_ROOT, toDelete.getId());

        // Do DELETE, we should get OK and the account that's deleted as the entity
        Response response1 = target(URI).request().delete();
        Account accountFromResponse = response1.readEntity(Account.class);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response1.getStatus());
        Assert.assertEquals(toDelete, accountFromResponse);

        // Try GET on the id, we should get a 404
        Response response2 = target(URI).request().get();
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response2.getStatus());

        // Since all is working well, delete the created account from testAccounts.
        // This is needed because other tests use testAccounts to validate the service's data.
        testAccounts.remove(0);
    }

    /**
     * DELETE
     *
     * DELETE /accounts/{id}
     *
     */
    @Test
    public void testDeleteAccountWithInvalidIDReturns404NotFound() {
        // Test PreRequisite
        Assert.assertTrue(testAccounts.size() > 1);

        UUID idToDelete = UUID.randomUUID();

        final String URI = String.format("%s/%s", AccountResource.ACCOUNTS_ROOT, idToDelete);

        // Do DELETE, we should get OK
        Response response1 = target(URI).request().delete();
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response1.getStatus());
    }

    /**
     * TRANSFER
     *
     * POST /accounts/{id1}/transfer/{id2}
     *
     * Check that a normal transfer completes successfully.
     */
    @Test
    public void testSuccessfulTransfer() {
        // Test PreRequisite
        Assert.assertTrue(testAccounts.size() > 1);

        final Account fromAccount = testAccounts.get(0);
        final Account toAccount = testAccounts.get(1);

        final Double AMOUNT = 500.0;
        Transfer transfer = new Transfer();
        transfer.setFrom(fromAccount);
        transfer.setTo(toAccount);
        transfer.setDate(new Date());
        transfer.setAmount(AMOUNT);

        // Make POST request and read the response
        final String URI = String.format("/accounts/%s/transfer/%s", fromAccount.getId(), toAccount.getId());
        Entity toPost = Entity.entity(transfer, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);

        // Check that we get a 200 OK in the response
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Transfer transferFromResponse = response.readEntity(Transfer.class);

        // Manually set the id and date from the original transfer
        transfer.setId(transferFromResponse.getId());
        transfer.setDate(transferFromResponse.getDate());

        // Check that both transfer objects match
        Assert.assertEquals(transfer, transferFromResponse);
    }

    /**
     * TRANSFER
     *
     * POST /accounts/{id1}/transfer/{id2}
     *
     * Checks that a Bad Request is returned when account does not have enough funds.
     */
    @Test
    public void testTransferWhenAccountHasNotEnoughFundsReturns400BadRequest() {
        // Test PreRequisite
        Assert.assertTrue(testAccounts.size() > 1);

        final Account fromAccount = testAccounts.get(0);
        final Account toAccount = testAccounts.get(1);

        // set toTransfer to be something greater than fromAccount's balance
        final Double amountToTransfer = fromAccount.getBalance() + 100.0f;

        Transfer transfer = new Transfer();
        transfer.setFrom(fromAccount);
        transfer.setTo(toAccount);
        transfer.setDate(new Date());
        transfer.setAmount(amountToTransfer);

        // Make  POST request and read the response
        final String URI = String.format("/accounts/%s/transfer/%s", fromAccount.getId(), toAccount.getId());
        Entity toPost = Entity.entity(transfer, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);

        // Check that we get a 400 Bad Request in the response
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    /**
     * TRANSFER
     *
     * POST /accounts/{id1}/transfer/{id2}
     *
     * Checks that a Bad Request is returned when the amount in Transfer is negative.
     */
    @Test
    public void testTransferWhenAmountIsNegativeReturns400BadRequest() {
        // Test PreRequisite
        Assert.assertTrue(testAccounts.size() > 1);

        final Account fromAccount = testAccounts.get(0);
        final Account toAccount = testAccounts.get(1);

        // set toTransfer to be something greater than fromAccount's balance
        final Double amountToTransfer = -fromAccount.getBalance();

        Transfer transfer = new Transfer();
        transfer.setFrom(fromAccount);
        transfer.setTo(toAccount);
        transfer.setDate(new Date());
        transfer.setAmount(amountToTransfer);

        // Make  POST request and read the response
        final String URI = String.format("/accounts/%s/transfer/%s", fromAccount.getId(), toAccount.getId());
        Entity toPost = Entity.entity(transfer, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);

        // Check that we get a 400 Bad Request in the response
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    /**
     * TRANSFER
     *
     * POST /accounts/{id1}/transfer/{id2}
     *
     * Check that 400 Bad Request is returned when {@code from} and {@code to} accounts are the same account.
     */
    @Test
    public void testTransferReturnsBadRequestWhenFromAccountAndToAccountsAreTheSame() {
        // Test PreRequisite
        Assert.assertTrue(testAccounts.size() > 1);

        final Account fromAccount = testAccounts.get(0);
        final Account toAccount = fromAccount;

        // set toTransfer to be something greater than fromAccount's balance
        final Double amountToTransfer = fromAccount.getBalance();

        Transfer transfer = new Transfer();
        transfer.setFrom(fromAccount);
        transfer.setTo(toAccount);
        transfer.setDate(new Date());
        transfer.setAmount(amountToTransfer);

        // Make  POST request and read the response
        final String URI = String.format("/accounts/%s/transfer/%s", fromAccount.getId(), toAccount.getId());
        Entity toPost = Entity.entity(transfer, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);

        // Check that we get a 400 Bad Request in the response
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    /**
     * TRANSFER
     *
     * POST /accounts/{id1}/transfer/{id2}
     *
     * Check that 404 Not Found is returned when {@code from} account is null.
     */
    @Test
    public void testTransferReturnsNotFoundWhenFromAccountIsNull() {
        // Test PreRequisite
        Assert.assertTrue(testAccounts.size() > 0);

        final Account toAccount = testAccounts.get(0);

        UUID fromId = UUID.randomUUID();

        final Double AMOUNT = 500.0;
        Transfer transfer = new Transfer();
        transfer.setFrom(null);
        transfer.setTo(toAccount);
        transfer.setDate(new Date());
        transfer.setAmount(AMOUNT);

        // Make  POST request and read the response
        final String URI = String.format("/accounts/%s/transfer/%s", fromId, toAccount.getId());
        Entity toPost = Entity.entity(transfer, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);

        // Check that we get a 404 Not Found in the response
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * TRANSFER
     *
     * POST /accounts/{id1}/transfer/{id2}
     *
     * Check that 404 Not Found is returned when {@code to} account is null.
     */
    @Test
    public void testTransferReturnsNotFoundWhenToAccountIsNull() {
        // Test PreRequisite
        Assert.assertTrue(testAccounts.size() > 0);

        final Account fromAccount = testAccounts.get(0);

        UUID toId = UUID.randomUUID();

        final Double AMOUNT = 500.0;
        Transfer transfer = new Transfer();
        transfer.setFrom(fromAccount);
        transfer.setTo(null);
        transfer.setDate(new Date());
        transfer.setAmount(AMOUNT);

        // Make  POST request and read the response
        final String URI = String.format("/accounts/%s/transfer/%s", fromAccount.getId(), toId);
        Entity toPost = Entity.entity(transfer, MediaType.APPLICATION_JSON);
        Response response = target(URI).request().post(toPost);

        // Check that we get a 404 Not Found in the response
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    /**
     * TRANSFER
     *
     * GET /accounts/{id}/transfers
     *
     * Check that GET returns all Transfers associated with the account whose Id is {id}.
     */
    @Test
    public void testGetAllTransfersFromAccount() {
        Account account1 = AccountFactory.createAccount("Checking Account", "Sami", 10000.0 );
        Account account2 = AccountFactory.createAccount("Savings Account", "John", 100.0 );
        Account account3 = AccountFactory.createAccount("Obamacare", "Dude", 1000.0 );

        // Create some transfers going from and to account1, account2
        //

        // account1 --> account2: 100.0
        Transfer t1 = new Transfer();
        t1.setId(UUID.randomUUID());
        t1.setDate(new Date());
        t1.setFrom(account1);
        t1.setTo(account2);
        t1.setAmount(100.0);

        // account2 --> account1: 10.0
        Transfer t2 = new Transfer();
        t2.setId(UUID.randomUUID());
        t2.setDate(new Date());
        t2.setFrom(account2);
        t2.setTo(account1);
        t2.setAmount(10.0);

        // account1 --> account3: 250.0
        Transfer t3 = new Transfer();
        t3.setId(UUID.randomUUID());
        t3.setDate(new Date());
        t3.setFrom(account1);
        t3.setTo(account3);
        t3.setAmount(250.0);

        // account2 --> account3: 20.0
        Transfer t4 = new Transfer();
        t4.setId(UUID.randomUUID());
        t4.setDate(new Date());
        t4.setFrom(account2);
        t4.setTo(account3);
        t4.setAmount(20.0);

        List<Transfer> transfers = new ArrayList<Transfer>() {{
            add(t1);
            add(t2);
            add(t3);
            add(t4);
        }};

        final String URI = String.format("accounts/%s/transfers", account1.getId());
        Response response = target(URI).request().get();
        List<Transfer> transfersFromResponse = response.readEntity(new GenericType<List<Transfer>>(){});

        // Check that we get 200 OK
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Check that only 3 transfers are returned in the responses
        Assert.assertEquals(3, transfersFromResponse.size());

        // Check that Transfer4 is not in that list but the rest are
        Assert.assertTrue(transfers.contains(t1));
        Assert.assertTrue(transfers.contains(t2));
        Assert.assertTrue(transfers.contains(t3));
        Assert.assertFalse(transfers.contains(t4));
    }

    /**
     * Check that if the provided {id} is non-existent then a 404 Not Found Response is returned
     */
    @Test
    public void testGetAllTransfersWithInvalidIdReturns404NotFound() {
        Account account1 = AccountFactory.createAccount("Checking Account", "Sami", 10000.0 );
        Account account2 = AccountFactory.createAccount("Obamacare", "Dude", 1000.0 );

        // Create some transfers going from and to account1, account2
        //

        // account1 --> account2: 100.0
        Transfer t1 = new Transfer();
        t1.setId(UUID.randomUUID());
        t1.setDate(new Date());
        t1.setFrom(account1);
        t1.setTo(account2);
        t1.setAmount(100.0);

        // account2 --> account1: 10.0
        Transfer t2 = new Transfer();
        t2.setId(UUID.randomUUID());
        t2.setDate(new Date());
        t2.setFrom(account2);
        t2.setTo(account1);
        t2.setAmount(10.0);

        final String URI = String.format("accounts/%s/transfers", account1.getId());
        Response response = target(URI).request().get();
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
}
