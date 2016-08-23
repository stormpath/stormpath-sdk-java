package com.stormpath.sdk.client

import com.stormpath.sdk.account.Account
import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.factor.Factor
import com.stormpath.sdk.impl.factor.sms.DefaultSmsFactorBuilder
import com.stormpath.sdk.impl.phone.DefaultPhoneBuilder
import com.stormpath.sdk.phone.Phone
import org.testng.annotations.Test

class FactorIT extends ClientIT {
    @Test
    void testCreateFactorWithNewPhone() {

        Directory dir = client.instantiate(Directory)
        dir.name = uniquify("Java SDK: DirectoryIT.testDeleteAccount")
        dir = client.currentTenant.createDirectory(dir)
        deleteOnTeardown(dir)

        def email = 'johndeleteme@nowhere.com'

        Account account = client.instantiate(Account)
        account = account.setGivenName('John')
                .setSurname('DELETEME')
                .setEmail(email)
                .setPassword('Changeme1!')

        dir.createAccount(account)

        Phone phone = new DefaultPhoneBuilder().setAccount(account).setNumber("+18008675309").build()

        Factor smsFactor = new DefaultSmsFactorBuilder().setAccount(account).setPhone(phone).build()
    }
}
