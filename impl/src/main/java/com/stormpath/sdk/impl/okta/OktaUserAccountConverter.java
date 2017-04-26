package com.stormpath.sdk.impl.okta;

import com.stormpath.sdk.account.AccountStatus;
import com.stormpath.sdk.account.EmailVerificationStatus;
import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Objects;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.okta.UserStatus;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts between Okta User map and Stormpath Account Map.
 */
public final class OktaUserAccountConverter {

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


    private static final String STORMPATH_EMAIL = "email";
    private static final String STORMPATH_HREF = "href";
    private static final String STORMPATH_USERNAME = "username";
    private static final String STORMPATH_PASSWORD = "password";
    private static final String STORMPATH_GIVEN_NAME = "givenName";
    private static final String STORMPATH_MIDDLE_NAME = "middleName";
    private static final String STORMPATH_SURNAME = "surname";
    private static final String STORMPATH_FULL_NAME = "fullName";
    private static final String STORMPATH_CREATED_AT = "createdAt";
    private static final String STORMPATH_MODIFIED_AT = "modifiedAt";
    private static final String STORMPATH_PASSWORD_MODIFIED_AT = "passwordModifiedAt";
    private static final String STORMPATH_STATUS = "status";
    private static final String STORMPATH_EMAIL_VERIFICATION_STATUS = "emailVerificationStatus";
    public static final  String STORMPATH_EMAIL_VERIFICATION_TOKEN = "emailVerificationToken";

    private static final String STORMPATH_CUSTOM_DATA = "customData";

    private static final String OKTA_ID = "id";
    private static final String OKTA_PROFILE = "profile";
    private static final String OKTA_LOGIN = "login";
    private static final String OKTA_EMAIL = "email";
    private static final String OKTA_FIRST_NAME = "firstName";
    private static final String OKTA_MIDDLE_NAME = "middleName";
    private static final String OKTA_LAST_NAME = "lastName";
    private static final String OKTA_CREATED = "created";
    private static final String OKTA_LAST_UPDATED = "lastUpdated";
    private static final String OKTA_PASSEWORD_CHANGED = "passwordChanged";
    private static final String OKTA_STATUS = "status";
    private static final String OKTA_LINKS = "_links";
    private static final String OKTA_SELF = "self";
    private static final String OKTA_HREF = "href";
    private static final String OKTA_EMAIL_VERIFICATION_STATUS = "emailVerificationStatus";
    private static final String OKTA_EMAIL_VERIFICATION_TOKEN = "emailVerificationToken";

    private static final String OKTA_CREDENTIALS = "credentials";
    private static final String OKTA_PASSWORD = "password";
    private static final String OKTA_VALUE = "value";

    public static final String RECOVERY_WORK_AROUND_KEY = "stormpathMigrationRecoveryAnswer";
    private static final String OKTA_RECOVERY_QUESTION = "recovery_question";
    private static final String OKTA_RECOVERY_QUESTION_QUESTION = "question";
    private static final String OKTA_RECOVERY_QUESTION_ANSWER = "answer";


    private OktaUserAccountConverter() {}

    public static Map<String, Object> toAccount(Map<String, Object> userMap, String baseUrl) {

        if (userMap == null) {
            return null;
        }

        // quick hack to make existing tests work.
        // if the UserMap does NOT contain an 'id' field, just assume the map is already an account.
        if (!userMap.containsKey("id")) {
            return userMap;
        }

        Map<String, Object> accountMap = new LinkedHashMap<>();
        nullSafePut(accountMap, STORMPATH_HREF, baseUrl + OktaApiPaths.USERS + userMap.get(OKTA_ID));
        Map<String, Object> profileMap = getPropertyMap(userMap, OKTA_PROFILE);

        if (!Collections.isEmpty(profileMap)) {
            nullSafePut(accountMap, STORMPATH_USERNAME, profileMap.get(OKTA_LOGIN));
            nullSafePut(accountMap, STORMPATH_EMAIL, profileMap.get(OKTA_EMAIL));
            nullSafePut(accountMap, STORMPATH_GIVEN_NAME, profileMap.get(OKTA_FIRST_NAME));
            nullSafePut(accountMap, STORMPATH_MIDDLE_NAME, profileMap.get(OKTA_MIDDLE_NAME));
            nullSafePut(accountMap, STORMPATH_SURNAME, profileMap.get(OKTA_LAST_NAME));
            nullSafePut(accountMap, STORMPATH_EMAIL_VERIFICATION_STATUS, fromEmailStatus(profileMap.get(OKTA_EMAIL_VERIFICATION_STATUS)));

            String emailVerificationToken = (String) profileMap.get(OKTA_EMAIL_VERIFICATION_TOKEN);
            if (!Strings.hasText(emailVerificationToken)) {
                Map<String, Object> verificationTokenMap = new LinkedHashMap<>();
                verificationTokenMap.put(STORMPATH_HREF, "/emailVerificationTokens/"+emailVerificationToken);
            }

            // build full name
            nullSafePut(accountMap, STORMPATH_FULL_NAME, buildFullName(profileMap.get(OKTA_FIRST_NAME), profileMap.get(OKTA_LAST_NAME)));
            // everything not in this lis is considered customData
            Map<String, Object> customData = trimMap(profileMap, OKTA_LOGIN, OKTA_EMAIL, OKTA_FIRST_NAME, OKTA_MIDDLE_NAME, OKTA_LAST_NAME, OKTA_EMAIL_VERIFICATION_STATUS, STORMPATH_EMAIL_VERIFICATION_TOKEN);
            customData.put(STORMPATH_HREF, baseUrl + OktaApiPaths.USERS + userMap.get(OKTA_ID) +"/okta-custom-data");
            nullSafePut(accountMap, STORMPATH_CUSTOM_DATA, customData);

        }

        nullSafePut(accountMap, STORMPATH_CREATED_AT, userMap.get(OKTA_CREATED));
        nullSafePut(accountMap, STORMPATH_MODIFIED_AT, userMap.get(OKTA_LAST_UPDATED));
        nullSafePut(accountMap, STORMPATH_PASSWORD_MODIFIED_AT, profileMap.get(OKTA_PASSEWORD_CHANGED));

        // UserStatus -> AccountStatus enum conversion
        if (userMap.containsKey(OKTA_STATUS)) {
            accountMap.put(STORMPATH_STATUS, fromUserStatus(userMap.get(OKTA_STATUS)));
        }


        // _links.self.href -> href
        nullSafePut(accountMap, STORMPATH_HREF, getOktaHref(userMap));

        Map<String, Object> groupsMap = new LinkedHashMap<>();
        groupsMap.put(STORMPATH_HREF, accountMap.get(STORMPATH_HREF) + "/groups");
        accountMap.put("groups", groupsMap);

        return accountMap;
    }

    public static Map<String, Object> toUser(Map<String, Object> accountMap) {

        Map<String, Object> userMap = new LinkedHashMap<>();
        Map<String, Object> profileMap = new LinkedHashMap<>();

        String username = (String) accountMap.get(STORMPATH_USERNAME);
        if (!Strings.hasText(username)) {
            username = (String) accountMap.get(STORMPATH_EMAIL);
        }
        nullSafePut(profileMap, OKTA_LOGIN, username);
        nullSafePut(profileMap, OKTA_EMAIL, accountMap.get(STORMPATH_EMAIL));
        nullSafePut(profileMap, OKTA_FIRST_NAME, accountMap.get(STORMPATH_GIVEN_NAME));
        nullSafePut(profileMap, OKTA_MIDDLE_NAME, accountMap.get(STORMPATH_MIDDLE_NAME));
        nullSafePut(profileMap, OKTA_LAST_NAME, accountMap.get(STORMPATH_SURNAME));
        nullSafePut(profileMap, OKTA_EMAIL_VERIFICATION_STATUS, accountMap.get(STORMPATH_EMAIL_VERIFICATION_STATUS));

        nullSafePut(userMap, OKTA_CREATED, accountMap.get(STORMPATH_CREATED_AT));
        nullSafePut(userMap, OKTA_LAST_UPDATED, accountMap.get(STORMPATH_MODIFIED_AT));
        nullSafePut(userMap, OKTA_PASSEWORD_CHANGED, accountMap.get(STORMPATH_PASSWORD_MODIFIED_AT));


        Map<String, Object> credentialsMap = new LinkedHashMap<>();
        // credentials
        if (accountMap.containsKey(STORMPATH_PASSWORD)) {

            Map<String, Object> passwordMap = new LinkedHashMap<>();

            userMap.put(OKTA_CREDENTIALS, credentialsMap);
            credentialsMap.put(OKTA_PASSWORD, passwordMap);
            passwordMap.put(OKTA_VALUE, accountMap.get(STORMPATH_PASSWORD));
        }

        // custom data, just drop it in profile map
        if (accountMap.containsKey(STORMPATH_CUSTOM_DATA)) {

            Map<String, Object> customData = trimMap(getPropertyMap(accountMap, STORMPATH_CUSTOM_DATA), STORMPATH_HREF);
            profileMap.putAll(customData);

            String recoveryAnswer = (String) profileMap.get(RECOVERY_WORK_AROUND_KEY);

            if (Strings.hasText(recoveryAnswer)) {
                // here is the work around, if we have a recovery answer we MUST set the question
                Map<String, Object> recoveryQuestionMap = new LinkedHashMap<>();
                recoveryQuestionMap.put(OKTA_RECOVERY_QUESTION_QUESTION, RECOVERY_WORK_AROUND_KEY); // the question is also the key
                recoveryQuestionMap.put(OKTA_RECOVERY_QUESTION_ANSWER, recoveryAnswer);

                credentialsMap.put(OKTA_RECOVERY_QUESTION, recoveryQuestionMap);
                userMap.put(OKTA_CREDENTIALS, credentialsMap);
            }
        }

        if (!Collections.isEmpty(profileMap)) {
            userMap.put(OKTA_PROFILE, profileMap);
        }

        return userMap;
    }

    private static String getOktaHref(Map<String, Object> properties) {

        // _links.self.href -> href
        Map<String, Object> linksMap = getPropertyMap(properties,OKTA_LINKS);
        if (!Collections.isEmpty(linksMap)) {
            Map<String, Object> self = getPropertyMap(linksMap, OKTA_SELF);
            if (!Collections.isEmpty(self)) {
                return (String) self.get(OKTA_HREF);
            }
        }
        return null;
    }

    private static String buildFullName(Object firstName, Object lastName) {
        return (Objects.getDisplayString(firstName) + " " + Objects.getDisplayString(lastName)).trim();
    }

    private static Map<String, Object> getPropertyMap(Map<String, Object> map, String key) {
        return (Map<String, Object>) map.get(key);
    }

    private static void nullSafePut(Map<String, Object> map, String key, Object value) {

        if (value != null) {
            map.put(key, value);
        }
    }

    private static String fromEmailStatus(Object emailStatusRaw) {

        if (emailStatusRaw == null) {
            return EmailVerificationStatus.UNKNOWN.name();
        }

        return EmailVerificationStatus.valueOf(Objects.nullSafeToString(emailStatusRaw)).name();
    }

    private static String fromUserStatus(Object userStatusRaw) {

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
    private static Map<String, Object> trimMap(Map<String, Object> map, String... keys) {

        Map<String, Object> result = new LinkedHashMap<>(map);
        for (String key : keys) {
            result.remove(key);
        }

        return result;
    }

    public static Map<String, Object> toStormpathGroup(Map<String, Object> oktaGroup) {

        if (Collections.isEmpty(oktaGroup) || !oktaGroup.containsKey(OKTA_PROFILE)) {
            return oktaGroup;
        }

        Map<String, Object> stormpathGroup = new LinkedHashMap<>();

        nullSafePut(stormpathGroup, STORMPATH_CREATED_AT, oktaGroup.get(OKTA_CREATED));
        nullSafePut(stormpathGroup, STORMPATH_MODIFIED_AT, oktaGroup.get(OKTA_LAST_UPDATED));

        stormpathGroup.put(STORMPATH_STATUS, "ENABLED");

        Map<String, Object> profile = getPropertyMap(oktaGroup, OKTA_PROFILE);
        if (!Collections.isEmpty(profile)) {
            nullSafePut(stormpathGroup, "name", profile.get("name"));
            nullSafePut(stormpathGroup, "description", profile.get("description"));
        }

        // _links.self.href -> href
        nullSafePut(stormpathGroup, STORMPATH_HREF, "/api/v1/groups/" + oktaGroup.get("id"));

        stormpathGroup.put(STORMPATH_CUSTOM_DATA, new LinkedHashMap<String, Object>());

        return stormpathGroup;
    }

}
