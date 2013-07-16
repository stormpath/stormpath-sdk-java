package com.stormpath.sdk.client

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.account.AccountList
import com.stormpath.sdk.account.AccountStatus
import com.stormpath.sdk.account.Accounts
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.application.ApplicationList
import com.stormpath.sdk.application.ApplicationStatus
import com.stormpath.sdk.application.Applications
import com.stormpath.sdk.directory.Directories
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.directory.DirectoryList
import com.stormpath.sdk.group.Group
import com.stormpath.sdk.group.GroupList
import com.stormpath.sdk.group.GroupStatus
import com.stormpath.sdk.lang.Strings
import com.stormpath.sdk.tenant.Tenant
import org.testng.annotations.Test

import static org.testng.Assert.assertFalse

/**
 * @since 0.8
 */
class HammerIT extends ClientIT {

    static Random random = new Random() //doesn't need to be secure - used for generating names

    static List<String> FIRST_NAMES = firstNameList()
    static List<String> LAST_NAMES = lastNameList()

    @Test
    public void dropTheHammer() {

        Tenant tenant = client.getCurrentTenant();

        //create a bunch of apps to test pagination:

        26.times { i ->
            def app = client.instantiate(Application)
            app.name = uniquify("Test Application")
            app.status = (i % 2 == 0 ? ApplicationStatus.ENABLED : ApplicationStatus.DISABLED)
            app.description = uniquify("Test Application Description")

            app = tenant.createApplication(Applications.newCreateRequestFor(app).createDirectory().build())

            //get the auto-created Directory and add some accounts and groups:
            def dirName = app.name + ' Directory'
            def list = tenant.getDirectories(Directories.where(Directories.name().eqIgnoreCase(dirName)))
            def iterator = list.iterator()
            Directory dir = iterator.next()
            assertFalse iterator.hasNext() //should only be the one matching dir

            //add some groups:
            def admin = client.instantiate(Group)
            admin.name = uniquify("Test Group Admin")
            dir.createGroup(admin)
            def users = client.instantiate(Group)
            users.name = uniquify("Test Group Users")
            dir.createGroup(users)
            def disabled = client.instantiate(Group)
            disabled.name = uniquify("Test Group Disabled")
            disabled.status = GroupStatus.DISABLED
            dir.createGroup(disabled)

            //add some users:

            25.times{ j ->

                def account;



                account = client.instantiate(Account)
                account.givenName = randomFirstName().trim()
                account.middleName = "IT Test"
                account.surname = randomLastName().trim()
                account.username = account.givenName.toLowerCase() + '-' + account.surname.toLowerCase() + '-' + UUID.randomUUID()
                account.email = account.username + '@gmail.com'
                account.password = "changeMe1!"

                if (j % 2 == 0) {
                    account.status = AccountStatus.DISABLED
                }

                dir.createAccount(account)

                if (j % 3 == 0) {
                    admin.addAccount(account)
                } else if (j % 2 == 0) {
                    disabled.addAccount(account)
                } else {
                    users.addAccount(account)
                }
            }
        }

        ApplicationList applications = tenant.getApplications();

        for (Application application : applications) {
            println "Application $application"
        }

        DirectoryList directories = tenant.getDirectories();

        for (Directory directory : directories) {
            directory.getName();
            println "Directory $directory";

            GroupList groupList = directory.getGroups();
            for (Group group : groupList) {
                group.getName()
                println("- Group $group");
            }

            AccountList accountList = directory.getAccounts()
            for (Account account : accountList) {
                println("-- Account $account");
            }
        }
    }

    @Test
    void testStuff() {
        Tenant tenant = client.getCurrentTenant()

        def apps = tenant.getApplications()
        def app = null
        for( def anApp : apps) {
            if (!anApp.name.equals('Stormpath')) {
                app = anApp
                break;
            }
        }

        def accts = app.getAccounts(Accounts.where(Accounts.status().eq(AccountStatus.ENABLED)).orderByEmail().descending().orderBySurname())
        accts.each { println(it) }
    }

    @Test
    void deleteEmAll() {

        Tenant tenant = client.getCurrentTenant();

        def apps = tenant.getApplications()
        apps.each { app ->
            if (!(app.name.equals('Stormpath'))) {
                app.delete()
            }
        }

        def dirs = tenant.getDirectories()
        dirs.each { dir ->
            if (!(dir.name.equals('Stormpath Administrators'))) {
                dir.delete()
            }
        }
    }

    static String randomFirstName() {
        return FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()))
    }

    static String randomLastName() {
        return LAST_NAMES.get(random.nextInt(LAST_NAMES.size()))

    }

    static List<String> firstNameList() {
        def names = '''
Adam
Adrian
Alan
Alexander
Andrew
Anthony
Austin
Benjamin
Blake
Boris
Brandon
Brian
Cameron
Carl
Charles
Christian
Christopher
Colin
Connor
Dan
David
Dominic
Dylan
Edward
Eric
Evan
Frank
Gavin
Gordon
Harry
Ian
Isaac
Jack
Jacob
Jake
James
Jason
Joe
John
Jonathan
Joseph
Joshua
Julian
Justin
Keith
Kevin
Leonard
Liam
Lucas
Luke
Matt
Max
Michael
Nathan
Neil
Nicholas
Oliver
Owen
Paul
Peter
Phil
Piers
Richard
Robert
Ryan
Sam
Sean
Sebastian
Simon
Stephen
Steven
Stewart
Thomas
Tim
Trevor
Victor
Warren
William
Abigail
Alexandra
Alison
Amanda
Amelia
Amy
Andrea
Angela
Anna
Anne
Audrey
Ava
Bella
Bernadette
Carol
Caroline
Carolyn
Chloe
Claire
Deirdre
Diana
Diane
Donna
Dorothy
Elizabeth
Ella
Emily
Emma
Faith
Felicity
Fiona
Gabrielle
Grace
Hannah
Heather
Irene
Jan
Jane
Jasmine
Jennifer
Jessica
Joan
Joanne
Julia
Karen
Katherine
Kimberly
Kylie
Lauren
Leah
Lillian
Lily
Lisa
Madeleine
Maria
Mary
Megan
Melanie
Michelle
Molly
Natalie
Nicola
Olivia
Penelope
Pippa
Rachel
Rebecca
Rose
Ruth
Sally
Samantha
Sarah
Sonia
Sophie
Stephanie
Sue
Theresa
Tracey
Una
Vanessa
Victoria
Virginia
Wanda
Wendy
Yvonne
Zoe
'''
        def result = [] as Set
        names.eachLine { s ->
            s = Strings.trimAllWhitespace(s)
            s = s.trim()
            if (Strings.hasLength(s)) {
                result.add(s)
            }
            return null;
        }
        return result as List
    }

    static List<String> lastNameList() {
        def names = '''
Abraham
Allan
Alsop
Anderson
Arnold
Avery
Bailey
Baker
Ball
Bell
Berry
Black
Blake
Bond
Bower
Brown
Buckland
Burgess
Butler
Cameron
Campbell
Carr
Chapman
Churchill
Clark
Clarkson
Coleman
Cornish
Davidson
Davies
Dickens
Dowd
Duncan
Dyer
Edmunds
Ellison
Ferguson
Fisher
Forsyth
Fraser
Gibson
Gill
Glover
Graham
Grant
Gray
Greene
Hamilton
Hardacre
Harris
Hart
Hemmings
Henderson
Hill
Hodges
Howard
Hudson
Hughes
Hunter
Ince
Jackson
James
Johnston
Jones
Kelly
Kerr
King
Knox
Lambert
Langdon
Lawrence
Lee
Lewis
Lyman
MacDonald
Mackay
Mackenzie
MacLeod
Manning
Marshall
Martin
Mathis
May
McDonald
McLean
McGrath
Metcalfe
Miller
Mills
Mitchell
Morgan
Morrison
Murray
Nash
Newman
Nolan
North
Ogden
Oliver
Paige
Parr
Parsons
Paterson
Payne
Peake
Peters
Piper
Poole
Powell
Pullman
Quinn
Rampling
Randall
Rees
Reid
Roberts
Robertson
Ross
Russell
Rutherford
Sanderson
Scott
Sharp
Short
Simpson
Skinner
Slater
Smith
Springer
Stewart
Sutherland
Taylor
Terry
Thomson
Tucker
Turner
Underwood
Vance
Vaughan
Walker
Wallace
Walsh
Watson
Welch
White
Wilkins
Wilson
Wright
Young
'''
        def result = [] as Set
        names.eachLine {
            def s = Strings.trimAllWhitespace(it)
            s = s.trim()
            if (Strings.hasLength(s)) {
                result.add(s)
            }
            return null;
        }
        return result as List
    }
}
