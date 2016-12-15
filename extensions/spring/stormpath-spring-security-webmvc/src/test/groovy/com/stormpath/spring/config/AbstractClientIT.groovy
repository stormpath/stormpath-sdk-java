package com.stormpath.spring.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.stormpath.sdk.account.Account
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.Client
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.resource.Deletable
import com.stormpath.spring.model.GuerillaEmail
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass

import static com.jayway.restassured.RestAssured.get

/**
 * @since 1.2.3
 */
abstract class AbstractClientIT extends AbstractTestNGSpringContextTests {

    private static final String GUERILLA_MAIL_BASE = "http://api.guerrillamail.com/ajax.php"
    
    @Autowired
    Client client

    @Autowired
    Application application

    //State shared by these internal tests
    def password = "Pass123!" + UUID.randomUUID()
    Account account

    ///Supporting properties and methods
    List<Deletable> resourcesToDelete

    protected Account createTempAccount(String password) {
        def username = "foo-account-deleteme-" + UUID.randomUUID()
        def email = username + "@testmail.stormpath.com"
        return createTempAccount(username, email, password)
    }

    protected Account createTempAccount(String username, String email, String password) {
        Account account = client.instantiate(Account.class)
        account.setEmail(email)
        account.setUsername(username)
        account.setPassword(password)
        account.setGivenName(username)
        account.setSurname(username)
        application.createAccount(account)
        deleteOnTeardown(account)
        return account
    }

    protected Directory createTempDir() {
        Directory dir = client.instantiate(Directory.class)
        String name = "foo-dir-deleteme-" + UUID.randomUUID()
        dir.setName(name)
        client.createDirectory(dir)
        deleteOnTeardown(dir)
        return dir
    }

    protected void deleteOnTeardown(Deletable d) {
        this.resourcesToDelete.add(d)
    }

    protected GuerillaEmail getGuerrillaEmail() throws IOException {
        String json = get(GUERILLA_MAIL_BASE + "?f=get_email_address").asString()

        ObjectMapper mapper = new ObjectMapper()
        return mapper.readValue(json, GuerillaEmail)
    }
    
    protected Document retrieveEmail(GuerillaEmail guerillaEmail) {
        String json = get(GUERILLA_MAIL_BASE + "?f=get_email_list&offset=0&sid_token=" + guerillaEmail.getToken()).asString()
        ObjectMapper mapper = new ObjectMapper()
        JsonNode rootNode = mapper.readTree(json)
        JsonNode emailList = rootNode.path("list")

        String emailId = null
        int count = 0

        while (emailId == null && count++ < 30) {
            for (JsonNode emailNode : emailList) {
                String mailFrom = emailNode.get("mail_from").asText()
                String localEmailId = emailNode.get("mail_id").asText()
                if (mailFrom.contains("stormpath.com")) {
                    emailId = localEmailId
                    break
                }
            }
            if (emailId == null) { // try retrieving email again
                Thread.sleep(500)
                json = get(GUERILLA_MAIL_BASE + "?f=get_email_list&offset=0&sid_token=" + guerillaEmail.getToken()).asString()
                rootNode = mapper.readTree(json)
                emailList = rootNode.path("list")
            }
        }

        if (emailId == null) { return null }

        // fetch stormpath email content
        json = get(GUERILLA_MAIL_BASE + "?f=fetch_email&sid_token=" + guerillaEmail.getToken() + "&email_id=" + emailId).asString()
        rootNode = mapper.readTree(json)
        String emailBody = rootNode.get("mail_body").asText()
        return Jsoup.parse(emailBody)
    }


    @BeforeClass
    void setUp() {
        resourcesToDelete = []
        def dir = createTempDir()
        application.setDefaultAccountStore(dir)
        account = createTempAccount(password)
    }

    @AfterClass
    void tearDown() {
        def reversed = resourcesToDelete.reverse() //delete in opposite order (cleaner - children deleted before parents)
        reversed.collect { it.delete() }
    }

}
