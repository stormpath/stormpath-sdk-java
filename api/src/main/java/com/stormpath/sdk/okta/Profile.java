package com.stormpath.sdk.okta;

import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Saveable;

import java.util.Map;

/**
 * Okta User Profile wrapper
 */
public interface Profile extends Map<String, Object> {


    String getLogin();
    void setLogin(String login);

    String getFirstName();
    void setFirstName(String firstName);


    String getLastName();
    void setLastName(String lastName);

    String getMiddleName();
    void setMiddleName(String middleName);

    String getEmail();
    void setEmail(String email);

    String getDisplayName();
    void setDisplayName(String displayName);

/*

  // Other Okta profile attributes not yet added above.

    Base
    Honorific prefix	honorificPrefix
            string
    Base
    Honorific suffix	honorificSuffix
            string
    Base
    Title	title
    string

    Base
    Nickname	nickName
    string
            Base
    Profile Url	profileUrl
            string
    Base
    Secondary email	secondEmail
            string
    Base
    Mobile phone	mobilePhone
            string
    Base
    Primary phone	primaryPhone
            string
    Base
    Street address	streetAddress
            string
    Base
    City	city
    string
            Base
    State	state
    string
            Base
    Zip code	zipCode
            string
    Base
    Country code	countryCode
            string
    Base
    Postal Address	postalAddress
            string
    Base
    Preferred language	preferredLanguage
            string
    Base
    Locale locale
    string
            Base
    Time zone	timezone
            string
    Base
    User type	userType
            string
    Base
    Employee number	employeeNumber
            string
    Base
    Cost center	costCenter
            string
    Base
    Organization organization
    string
            Base
    Division	division
    string
            Base
    Department	department
    string
            Base
    ManagerId	managerId
    string
            Base
    Manager	manager
    string
            Base
 */
}
