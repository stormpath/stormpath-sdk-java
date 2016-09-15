package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.ChallengeList;
import com.stormpath.sdk.factor.FactorType;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.factor.AbstractFactor;
import com.stormpath.sdk.impl.resource.CollectionReference;
import com.stormpath.sdk.impl.resource.Property;
import com.stormpath.sdk.impl.resource.ResourceReference;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.phone.Phone;
import com.stormpath.sdk.resource.ResourceException;

import java.util.Map;

/**
 * Created by mehrshadrafiei on 9/1/16.
 */

// todo: mehrshad

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
    public Challenge getChallenge() {
        return getResourceProperty(CHALLENGE);
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

    protected FactorType getConcreteFactorType() {
        return FactorType.SMS;
    }

}
