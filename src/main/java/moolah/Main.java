package moolah;

import moolah.model.Account;
import moolah.model.AccountFactory;
import moolah.resources.AccountResource;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class Main {

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";
    static AccountResource service;

    public static HttpServer startServer() {

        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.rest package
        final ResourceConfig rc = new ResourceConfig().packages("moolah");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        // create AccountResource and add accounts to it
        service = new AccountResource();

        Account account1 = AccountFactory.createAccount("Investment", "Zulu", 15000.0);
        Account account2 = AccountFactory.createAccount("Checking", "Marwan", 4000.0);
        Account account3 = AccountFactory.createAccount("Checking", "Sami", 2000.0);

        // we fix the Ids of the account as it makes it easier for manual testing
        account1.setId(UUID.fromString("263afea3-3843-4880-b1d5-cce977be06c1"));
        account2.setId(UUID.fromString("960b5a20-8201-4f14-9012-6f388e6313e3"));
        account3.setId(UUID.fromString("2562e2ad-15a0-493f-a003-878e6cd43670"));
        service.addAccount(account1);
        service.addAccount(account2);
        service.addAccount(account3);

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}
