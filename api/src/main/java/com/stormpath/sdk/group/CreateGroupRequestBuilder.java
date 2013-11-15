package com.stormpath.sdk.group;

/**
 * A Builder to construct {@link com.stormpath.sdk.group.CreateGroupRequest}s.
 *
 * @see com.stormpath.sdk.application.Application#createGroup(com.stormpath.sdk.group.CreateGroupRequest)
 * @since 0.9
 */
public interface CreateGroupRequestBuilder {

    /**
     *
     * @return the builder instance for method chaining.
     */
    CreateGroupRequestBuilder withCustomData();

    /**
     * Creates a new {@code CreateGroupRequest} instance based on the current builder state.
     *
     * @return a new {@code CreateGroupRequest} instance based on the current builder state.
     */
    CreateGroupRequest build();
}
