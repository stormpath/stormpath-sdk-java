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
package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.ChallengeCriteria;
import com.stormpath.sdk.challenge.ChallengeList;
import com.stormpath.sdk.challenge.CreateChallengeRequest;
import com.stormpath.sdk.factor.FactorType;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.factor.AbstractFactor;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.phone.Phone;
import com.stormpath.sdk.query.Criteria;
import com.stormpath.sdk.resource.ResourceException;

import java.util.Map;

/**
 * @since 1.1.0
 */
public class DefaultSmsFactor extends AbstractFactor implements SmsFactor {
    static final ResourceReference<Phone> PHONE = new ResourceReference<>("phone", Phone.class);
    static final ResourceReference<Challenge> CHALLENGE = new ResourceReference<>("challenge", Challenge.class);
    static final ResourceReference<Challenge> MOST_RECENT_CHALLENGE = new ResourceReference<>("mostRecentChallenge", Challenge.class);
    static final CollectionReference<ChallengeList, Challenge> CHALLENGES =
            new CollectionReference<>("challenges", ChallengeList.class, Challenge.class);

    static final Map<String, Property> PROPERTY_DESCRIPTORS = createPropertyDescriptorMap(PHONE, CHALLENGE, MOST_RECENT_CHALLENGE, CHALLENGES);

    public DefaultSmsFactor(InternalDataStore dataStore) {
        super(dataStore);
    }

    public DefaultSmsFactor(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
    }

    @Override
    public Map<String, Property> getPropertyDescriptors() {
        PROPERTY_DESCRIPTORS.putAll(super.getPropertyDescriptors());
        return PROPERTY_DESCRIPTORS;
    }

    @Override
    public Phone getPhone() {
        return getResourceProperty(PHONE);
    }

    @Override
    public SmsFactor setPhone(Phone phone) {
        if(phone.getHref() != null) {
            setResourceProperty(PHONE, phone);
        }
        else{
            setMaterializableResourceProperty(PHONE, phone);
        }
        return this;
    }

    @Override
    public Challenge getMostRecentChallenge() {
        return getResourceProperty(MOST_RECENT_CHALLENGE);
    }

    @Override
    public SmsFactor setChallenge(Challenge challenge) {
        if(challenge.getHref() != null) {
            setResourceProperty(CHALLENGE, challenge);
        }
        else{
            setMaterializableResourceProperty(CHALLENGE, challenge);
        }
        return this;
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
    public ChallengeList getChallenges(Map<String, Object> queryParams) {
        ChallengeList list = getChallenges(); //safe to get the href: does not execute a query until iteration occurs
        return getDataStore().getResource(list.getHref(), ChallengeList.class, queryParams);
    }

    @Override
    public SmsFactor challenge() {
        String href = getHref();
        href += "/challenges";
        Assert.notNull(href, "SmsFactor hast to be materialized and have an href.");
        return getDataStore().create(href, this);
    }

    @Override
    public Challenge createChallenge(Challenge challenge) throws ResourceException {
        Assert.notNull(challenge, "Challenge cannot be null.");
        String href = getChallenges().getHref();
        return getDataStore().create(href, challenge);
    }

    @Override
    public Challenge createChallenge(CreateChallengeRequest request) throws ResourceException {
        Assert.notNull(request, "Request cannot be null.");

        final Challenge challenge = request.getChallenge();
        String href = getChallenges().getHref();

        if (request.hasChallengeOptions()) {
            return  getDataStore().create(href, challenge, request.getChallengeOptions());
        }
        return getDataStore().create(href, challenge);
    }

    protected FactorType getConcreteFactorType() {
        return FactorType.SMS;
    }
}
