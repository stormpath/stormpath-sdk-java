package com.stormpath.sdk.cache;

/**
 * Marker interface to enable caching for Okta Response objects.  Typically this API will ONLY cache
 * objects that have an 'href' property (which Okta responses do NOT have)
 */
public interface OktaCacheable {
}
