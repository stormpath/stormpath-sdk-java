package com.stormpath.sdk.application;

/**
 * A Builder to construct {@link CreateApplicationRequest}s.
 *
 * @see com.stormpath.sdk.tenant.Tenant#createApplication(CreateApplicationRequest)
 * @since 0.8
 */
public interface CreateApplicationRequestBuilder {

    /**
     * Directive to also create a new Directory for the new Application's needs.  The new Directory will automatically
     * be assigned as the Application's default login source.
     * <p/>
     * The directory will be automatically named based on heuristics to ensure a guaranteed unique name based on the
     * application.  If you want to specify the Directory's name, use the {@link #createDirectoryNamed(String)} method.
     *
     * @return the builder instance for method chaining.
     * @see #createDirectoryNamed(String)
     */
    CreateApplicationRequestBuilder createDirectory();

    /**
     * Directive to also create a new Directory (with the specified {@code directoryName} for the new Application's
     * needs.  The new Directory will automatically be assigned as the Application's default login source.
     * <p/>
     * If you don't care about the new Directory's name and want a reasonable default assigned automatically, don't
     * call this method - call {@link #createDirectory()} instead.
     *
     * @return the builder instance for method chaining.
     * @see #createDirectory()
     */
    CreateApplicationRequestBuilder createDirectoryNamed(String name);

    /**
     * Creates a new {@code CreateApplicationRequest} instance based on the current builder state.
     *
     * @return a new {@code CreateApplicationRequest} instance based on the current builder state.
     */
    CreateApplicationRequest build();
}
