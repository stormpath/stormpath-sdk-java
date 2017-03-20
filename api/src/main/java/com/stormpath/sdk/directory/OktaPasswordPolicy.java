package com.stormpath.sdk.directory;

import com.stormpath.sdk.resource.Resource;

import java.util.Date;
import java.util.Map;

public interface OktaPasswordPolicy extends Resource {

    String getType();
    String getId();
    String getStatus();
    String getName();
    String getDescription();
    int getPriority();
    boolean getSystem();
    Map<String, Object> getConditions();
    Date getCreated();
    Date getLastUpdated();
    Map<String, Object> getSettings();
    Map<String, Object> getDelegation();
    Map<String, Object> getRules();
}
