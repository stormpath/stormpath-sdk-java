package com.stormpath.sdk.impl.query;

import java.util.List;

/**
 * @since 0.8
 */
public interface Expandable {

    List<Expansion> getExpansions();
}
