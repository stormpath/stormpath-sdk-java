package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Objects;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.okta.UserStatus;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts between Okta User map and Stormpath Account Map.
 */
public class OktaUserAccountConverter {

    // https://docs.stormpath.com/rest/product-guide/latest/reference.html#account

    /*
    Stormpath Account field     | Okta User field
   -----------------------------|--------------------------------
    href                        | _links.self.href
    username                    | profile.login
    email                       | profile.email
    password                    | credentials.password.value
    givenName                   | profile.firstName
    middleName                  | profile.middleName
    surname                     | profile.lastName
    fullName                    | profile.firstName profile.lastName
    status                      | <enum conversion of `status`>
    createdAt                   | created
    modifiedAt                  | lastUpdated
    emailVerificationStatus     | <? possibly tied to status>
    emailVerificationToken      | <not supported>
    passwordModifiedAt          | passwordChanged
    customData                  | <`profile` mapped stripped of known fields>
    providerData                | ??
    directory                   | ??
    tenant                      | ??
    groups                      | ?
    groupMemberships            | ?
    applications                | ?
    apiKeys                     | <not supported>
    accessTokens                | <not supported>
    refreshTokens               | <not supported>
    linkedAccounts              | <not supported>
    accountLinks                | <not supported>
    phones                      | <not supported>
    factors                     | <not supported>


    Account Status              | User Status
   -----------------------------|-----------------
    ENABLED                     | ACTIVE
    DISABLED                    | DEPROVISIONED
    DISABLED                    | LOCKED_OUT
    DISABLED                    | PASSWORD_EXPIRED
    UNVERIFIED                  | PROVISIONED
    UNVERIFIED                  | RECOVERY
    UNVERIFIED                  | STAGED
    DISABLED                    | SUSPENDED

    Email Verification Status   | ?
   -----------------------------|-----------------
    ENABLED                     | ?
    DISABLED                    | ?
    UNVERIFIED                  | ?

    */

    public Map<String, Object> toAccount(Map<String, Object> userMap, String baseUrl) {

        // quick hack to make existing tests work.
        // if the UserMap does NOT contain an 'id' field, just assume the map is already an account.
        if (!userMap.containsKey("id")) {
            return userMap;
        }

        Map<String, Object> accountMap = new LinkedHashMap<>();
        nullSafePut(accountMap, "href", baseUrl + "/api/v1/users/" + userMap.get("id")); // TODO use stop putting /api/v1 all over the place
        Map<String, Object> profileMap = (Map<String, Object>) userMap.get("profile");

        if (!Collections.isEmpty(profileMap)) {
            nullSafePut(accountMap, "username", profileMap.get("login"));
            nullSafePut(accountMap, "email", profileMap.get("email"));

            nullSafePut(accountMap, "givenName", profileMap.get("firstName"));
            nullSafePut(accountMap, "middleName", profileMap.get("middleName"));
            nullSafePut(accountMap, "surname", profileMap.get("lastName"));
            nullSafePut(accountMap, "fullName", profileMap.get("firstName") + " " + profileMap.get("lastName")); // TODO: add null/format checks needed here

            nullSafePut(accountMap, "customData", trimMap(profileMap, "login", "email", "firstName", "middleName", "lastName"));
        }

        nullSafePut(accountMap, "createdAt", userMap.get("created"));
        nullSafePut(accountMap, "modifiedAt", userMap.get("lastUpdated"));
        nullSafePut(accountMap, "passwordModifiedAt", profileMap.get("passwordChanged"));

        // UserStatus -> AccountStatus enum conversion
        if (userMap.containsKey("status")) {
            accountMap.put("status", fromUserStatus(userMap.get("status")));
        }

        // _links.self.href -> href
        Map<String, Object> linksMap = (Map<String, Object>) userMap.get("_links");
        if (!Collections.isEmpty(linksMap)) {
            Map<String, Object> self = (Map<String, Object>) linksMap.get("self");
            if (!Collections.isEmpty(self)) {
                nullSafePut(accountMap, "href", self.get("href"));
            }
        }

        // pretty sure this logic is NOT needed when mapping User -> Account
//        Map<String, Object> credentialsMap = (Map<String, Object>) userMap.get("credentials");
//        if (!Collections.isEmpty(profileMap)) {
//            Map<String, Object> passwordMap = (Map<String, Object>) userMap.get("password");
//            if (!Collections.isEmpty(passwordMap) ) {
//                nullSafePut(accountMap, "password", profileMap.get("value"));
//            }
//        }

        return accountMap;
    }

    public Map<String, Object> toUser(Map<String, Object> accountMap) {

        Map<String, Object> userMap = new LinkedHashMap<>();
        Map<String, Object> profileMap = new LinkedHashMap<>();
        userMap.put("profile", profileMap);

        String username = (String) accountMap.get("username");
        if (!Strings.hasText(username)) {
            username = (String) accountMap.get("email");
        }
        nullSafePut(profileMap, "login", username);
        nullSafePut(profileMap, "email", accountMap.get("email"));

        nullSafePut(profileMap, "firstName", accountMap.get("givenName"));
        nullSafePut(profileMap, "middleName", accountMap.get("middleName"));
        nullSafePut(profileMap, "lastName", accountMap.get("surname"));
//        nullSafePut(userMap, "displayName", accountMap.get("fullName")); // generated field from Stormpath
        nullSafePut(userMap, "created", accountMap.get("createdAt"));
        nullSafePut(userMap, "lastUpdated", accountMap.get("modifiedAt"));
        nullSafePut(userMap, "passwordChanged", accountMap.get("passwordModifiedAt"));

        // credentials
        if (accountMap.containsKey("password")) {

            Map<String, Object> credentialsMap = new LinkedHashMap<>();
            Map<String, Object> passwordMap = new LinkedHashMap<>();

            userMap.put("credentials", credentialsMap);
            credentialsMap.put("password", passwordMap);
            passwordMap.put("value", accountMap.get("password"));
        }

        // custom data, just drop it in profile map
        if (accountMap.containsKey("customData")) {
            profileMap.putAll((Map<String, Object>) accountMap.get("customData"));
        }

        return userMap;
    }

    private void nullSafePut(Map<String, Object> map, String key, Object value) {

        if (value != null) {
            map.put(key, value);
        }
    }

    private String fromUserStatus(Object userStatusRaw) {

        UserStatus userStatus = UserStatus.valueOf(Objects.nullSafeToString(userStatusRaw));
        AccountStatus accountStatus;

       // TODO: handle email verification status at the same time?

        switch (userStatus) {
            case ACTIVE:
                accountStatus = AccountStatus.ENABLED;
                break;
            case DEPROVISIONED:
                accountStatus = AccountStatus.DISABLED;
                break;
            case LOCKED_OUT:
                accountStatus = AccountStatus.DISABLED;
                break;
            case PASSWORD_EXPIRED:
                accountStatus = AccountStatus.DISABLED;
                break;
            case PROVISIONED:
                accountStatus = AccountStatus.UNVERIFIED;
                break;
            case RECOVERY:
                accountStatus = AccountStatus.UNVERIFIED;
                break;
            case STAGED:
                accountStatus = AccountStatus.UNVERIFIED;
                break;
            case SUSPENDED:
                accountStatus = AccountStatus.DISABLED;
                break;
            default:
                accountStatus = AccountStatus.DISABLED;
                break;
        }
        return accountStatus.toString();
    }

    /**
     * Creates new map based on previous with all <code>keys</code> removed.
     * @param map source map
     * @param keys keys to remove from original map
     * @return new map based on previous with all <code>keys</code> removed.
     */
    private Map<String, Object> trimMap(Map<String, Object> map, String... keys) {

        Map<String, Object> result = new LinkedHashMap<>(map);
        for (String key : keys) {
            result.remove(key);
        }

        return result;
    }
}
