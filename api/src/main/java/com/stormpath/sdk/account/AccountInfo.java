package com.stormpath.sdk.account;

import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.group.GroupInfo;
import com.stormpath.sdk.group.GroupInfoList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by mzumbado on 4/18/16.
 */
public class AccountInfo {
    String href;
    String username;
    String email;
    String givenName;
    String middleName;
    String surname;
    String fullName;
    String status;
    Date createdAt;
    Date modifiedAt;
    String emailVerificationToken;
    GroupInfoList groups;

    public AccountInfo(Account account) {
        this.href = account.getHref();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.givenName = account.getGivenName();
        this.middleName = account.getMiddleName();
        this.surname = account.getSurname();
        this.fullName = account.getFullName();
        this.status = account.getStatus().name();
        this.createdAt = account.getCreatedAt();
        this.modifiedAt = account.getModifiedAt();
        this.emailVerificationToken = account.getEmailVerificationToken() != null? account.getEmailVerificationToken().getValue(): null;
        this.groups = new GroupInfoList();
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getEmailVerificationToken() {
        return emailVerificationToken;
    }

    public void setEmailVerificationToken(String emailVerificationToken) {
        this.emailVerificationToken = emailVerificationToken;
    }

    public void setGroups(Account account){
        this.groups.setGroupList(account.getGroups());
    }

    public GroupInfoList getGroups(){
        return groups;
    }
}
