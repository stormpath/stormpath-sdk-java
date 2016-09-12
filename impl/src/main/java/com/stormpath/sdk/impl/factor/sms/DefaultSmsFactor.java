package com.stormpath.sdk.impl.factor.sms;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.challenge.ChallengeList;
import com.stormpath.sdk.challenge.Challenges;
import com.stormpath.sdk.challenge.CreateChallengeRequest;
import com.stormpath.sdk.factor.FactorType;
import com.stormpath.sdk.factor.sms.SmsFactor;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.factor.AbstractFactor;
import com.stormpath.sdk.impl.resource.AbstractResource;
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
        // Set the factor type to 'SMS' once factor is instantiated via a client (getFactorType() == null).
        // But when the factor is instantiated via the resource factory as a consequence of a response from t
        // he back-end (getFactorType() != null), it should not be overwritten since it would mark the FactorType
        // as 'dirty'. This would not be correct and leads to unforeseen side effects.
        if(getFactorType() == null){
            setFactorType(FactorType.SMS);
        }
    }

    public DefaultSmsFactor(InternalDataStore dataStore, Map<String, Object> properties) {
        super(dataStore, properties);
        // Set the factor type to 'SMS' once factor is instantiated via a client (getFactorType() == null).
        // But when the factor is instantiated via the resource factory as a consequence of a response from t
        // he back-end (getFactorType() != null), it should not be overwritten since it would mark the FactorType
        // as 'dirty'. This would not be correct and leads to unforeseen side effects.
        if(getFactorType() == null){
            setFactorType(FactorType.SMS);
        }
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
    public Challenge createChallenge(CreateChallengeRequest request) throws ResourceException{

        Assert.notNull(request, "Request cannot be null.");

        final Challenge challenge = request.getChallenge();
        String href = getChallenges().getHref();

        return getDataStore().create(href, challenge);
    }

    @Override
    public Challenge createChallenge(Challenge challenge) throws ResourceException {
        Assert.notNull(challenge, "Phone instance cannot be null.");
        CreateChallengeRequest request = Challenges.newCreateRequestFor(challenge).build();
        return createChallenge(request);
    }

}
