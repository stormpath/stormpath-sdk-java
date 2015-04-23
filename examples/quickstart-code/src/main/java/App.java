import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.directory.CustomData;
import com.stormpath.sdk.resource.ResourceException;
import com.stormpath.sdk.tenant.Tenant;

import java.util.HashMap;
import java.util.Map;

/**
 * This class demonstrates the code found in the Stormpath Java SDK QuickStart
 * The main method receives one argument:
 * - The location of the apikey.properties file
 */
public class App {

    public static void main(String[] args) {

        String path = args[0];
        ApiKey apiKey = ApiKeys.builder().setFileLocation(path).build();

        // Instantiate a builder for your client and set required properties
        ClientBuilder builder = Clients.builder();
        builder.setApiKey(apiKey);

        // Build the client instance that you will use throughout your application code
        Client client = builder.build();

        // Obtain your default tenant
        Tenant tenant = client.getCurrentTenant();
        System.out.println("Current Tenant: " + tenant.getHref() + ", " + tenant.getName());

        // Retrieve your application
        ApplicationList applications = tenant.getApplications(
            Applications.where(Applications.name().eqIgnoreCase("My Application"))
        );

        Application application = applications.iterator().next();
        System.out.println("Application: " + application.getHref() + ", " + application.getName());

        // Create a User Account

        //Create the account object
        Account account = client.instantiate(Account.class);

        //Set the account properties
        account.setGivenName("Joe");
        account.setSurname("Stormtrooper");
        account.setUsername("tk421"); //optional, defaults to email if unset
        account.setEmail("tk421@stormpath.com");
        account.setPassword("Changeme1");
        CustomData customData = account.getCustomData();
        customData.put("favoriteColor", "white");

        // Create the account using the existing Application object
        application.createAccount(account);

        // Print account details

        System.out.println("Given Name: " + account.getGivenName());
        System.out.println("Favorite Color: " + account.getCustomData().get("favoriteColor"));

        // Search for a User Account

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("email", "tk421@stormpath.com");
        AccountList accounts = application.getAccounts(queryParams);
        account = accounts.iterator().next();

        System.out.println("Found Account: " + account.getHref() + ", " + account.getEmail());

        // Authenticate a User Account

        String usernameOrEmail = "tk421@stormpath.com";
        String rawPassword = "Changeme1";

        // Create an authentication request using the credentials
        AuthenticationRequest request = new UsernamePasswordRequest(usernameOrEmail, rawPassword);

        //Now let's authenticate the account with the application:
        try {
            AuthenticationResult result = application.authenticateAccount(request);
            account = result.getAccount();
            System.out.println("Authenticated Account: " + account.getUsername() + ", Email: " + account.getEmail());
        } catch (ResourceException ex) {
            System.out.println(ex.getStatus() + " " + ex.getMessage());
        }
    }
}
