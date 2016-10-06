/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.impl.factor;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.ChallengeCriteria;
import com.stormpath.sdk.challenge.ChallengeList;
import com.stormpath.sdk.challenge.CreateChallengeRequest;
import com.stormpath.sdk.factor.Factor;
import com.stormpath.sdk.factor.FactorStatus;
import com.stormpath.sdk.factor.FactorType;
import com.stormpath.sdk.factor.FactorVerificationStatus;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.resource.ResourceException;

import java.util.Date;
import java.util.Map;

/**
 * @since 1.1.0
 */
public abstract class AbstractFactor<T extends Challenge> extends AbstractInstanceResource implements Factor<T> {

    public static final EnumProperty<FactorType> TYPE = new EnumProperty<>("type", FactorType.class);
    public static final ResourceReference<Account> ACCOUNT = new ResourceReference<>("account", Account.class);
    public static final ResourceReference<? extends Challenge> CHALLENGE = new ResourceReference<>("challenge", Challenge.class);
    public static final ResourceReference<? extends Challenge> MOST_RECENT_CHALLENGE = new ResourceReference<>("mostRecentChallenge", Challenge.class);
    public static final CollectionReference<? extends ChallengeList, Challenge> CHALLENGES =
            new CollectionReference<>("challenges", ChallengeList.class, Challenge.class);
    public static final EnumProperty<FactorStatus> STATUS = new EnumProperty<>("status", FactorStatus.class);
    public static final EnumProperty<FactorVerificationStatus> VERIFICATION_STATUS = new EnumProperty<>("verificationStatus", FactorVerificationStatus.class);
    public static final DateProperty CREATED_AT = new DateProperty("createdAt");
    public static final DateProperty MODIFIED_AT = new DateProperty("modifiedAt");

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(TYPE, ACCOUNT, CHALLENGE, MOST_RECENT_CHALLENGE, CHALLENGES, STATUS, VERIFICATION_STATUS, CREATED_AT, MODIFIED_AT);

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    public AbstractFactor(InternalDataStore dataStore) {
        super(dataStore);// Set the factor type only one the factor is instantiated via a the client (i.e. client.instantiate(SmsFactor.class)).
        // However, when the factor is instantiated by the resource factory as a consequence of a response from the
        // back-end, it must not be overwritten since that would cause the FactorType to be marked as 'dirty'
        if(getType() == null){
            setType(getConcreteFactorType());
        }
    }

    public AbstractFactor(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);// Set the factor type only one the factor is instantiated via a the client (i.e. client.instantiate(SmsFactor.class)).
        // However, when the factor is instantiated by the resource factory as a consequence of a response from the
        // back-end, it must not be overwritten since that would cause the FactorType to be marked as 'dirty'
        if(getType() == null){
            setType(getConcreteFactorType());
        }
    }

    @Override
    public FactorStatus getStatus() {
        String value = getStringProperty(STATUS.getName());
        if (value == null) {
            return null;
        }
        return FactorStatus.valueOf(value.toUpperCase());
    }

    @Override
    public Factor setStatus(FactorStatus status) {
        setProperty(STATUS, status.name());
        return this;
    }

    @Override
    public FactorVerificationStatus getFactorVerificationStatus() {
        String value = getStringProperty(VERIFICATION_STATUS.getName());
        if (value == null) {
            return null;
        }
        return FactorVerificationStatus.valueOf(value.toUpperCase());
    }

    @Override
    public Factor setFactorVerificationStatus(FactorVerificationStatus verificationStatus) {
        setProperty(VERIFICATION_STATUS, verificationStatus.name());
        return this;
    }

    public FactorType getType() {
        String value = getStringProperty(TYPE.getName());
        if (value == null) {
            return null;
        }
        return FactorType.fromName(value);
    }

    public Factor setType(FactorType factorType) {
        setProperty(TYPE, factorType.getName());
        return this;
    }

    @Override
    public Account getAccount() {
        return getResourceProperty(ACCOUNT);
    }

    @Override
    public Factor setAccount(Account account) {
        setResourceProperty(ACCOUNT,account);
        return this;
    }

    @Override
    public T getMostRecentChallenge() {
        return (T) getResourceProperty(MOST_RECENT_CHALLENGE);
    }

    @Override
    public ChallengeList getChallenges() {
        return getResourceProperty(CHALLENGES);
    }

    @Override
    public ChallengeList getChallenges(ChallengeCriteria criteria) {
        ChallengeList list = getChallenges(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), ChallengeList.class, (Criteria<ChallengeCriteria>) criteria);
    }

    @Override
    public ChallengeList getChallenges(Map queryParams) {
        ChallengeList list = getChallenges(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), ChallengeList.class, queryParams);
    }

    @Override
    public Factor setChallenge(Challenge challenge) {
        if(challenge.getHref() != null) {
            setResourceProperty(CHALLENGE, challenge);
        }
        else{
            setMaterializableResourceProperty(CHALLENGE, challenge);
        }
        return this;
    }

    @Override
    public T createChallenge(Challenge challenge) throws ResourceException {
        Assert.notNull(challenge, "Challenge cannot be null.");
        String href = getChallenges().getHref();
        return (T) getDataStore().create(href, challenge);
    }

    @Override
    public T createChallenge(CreateChallengeRequest request) throws ResourceException {
        Assert.notNull(request, "Request cannot be null.");

        final Challenge challenge = request.getChallenge();
        String href = getChallenges().getHref();

        if (request.hasChallengeOptions()) {
            return (T) getDataStore().create(href, challenge, request.getChallengeOptions());
        }
        return (T) getDataStore().create(href, challenge);
    }

    @Override
    public void delete() {
        getDataStore().delete(this);
    }

    @Override
    public Date getCreatedAt() {
        return getDateProperty(CREATED_AT);
    }

    @Override
    public Date getModifiedAt() {
        return getDateProperty(MODIFIED_AT);
    }

    protected abstract FactorType getConcreteFactorType();
}
