package com.stormpath.sdk.impl.oauth;

import com.stormpath.sdk.oauth.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public final class ProviderClassMapping {

    public static final Map<String, Class<? extends Provider>> providerClassMap;

    static {
        Map<String, Class<? extends Provider>> map = new HashMap<String, Class<? extends Provider>>();
        //map.put(GoogleProviderRequest.PROVIDER_ID, GoogleProvider.class);
        //map.put(FacebookProviderRequest.PROVIDER_ID, FacebookProvider.class);
        providerClassMap = Collections.unmodifiableMap(map);
    }

}
