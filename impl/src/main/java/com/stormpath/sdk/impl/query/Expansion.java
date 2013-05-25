package com.stormpath.sdk.impl.query;

/**
 * An expansion represents a request to materialize a referenced resource within the requested resource itself.
 * <p/>
 * For example, an Account resource references its parent/owning directory, itself a complex object.  Normally when
 * returning an Account resource, the account's directory is represented as a linked resource, and the directory's
 * properties are not included in the Account's resource representation.
 * <p/>
 * If, when querying for the account, an expansion directive is specified (e.g. expand=directory), then the caller is
 * requesting that not only should the account be returned materialized but also that its referenced directory should
 * be included in the result, materialized as well.  For example:
 * <p/>
 * <h3>Default Behavior (no expansion)</h3>
 * <p/>
 * Request: GET /accounts/someAccountId<br/>
 * Response:
 * <pre>
 * {
 *     "href": "some/account/uri",
 *     "name": "foo",
 *     ...
 *     "directory": {
 *         "href": "the/directory/uri"
 *     }
 * }
 * </pre>
 * Notice that the directory property is a complex object with only an {@code href} property - i.e. it is a link only.
 * <h3>With Expansion</h3>
 * <p/>
 * Request: GET /accounts/someAccountId?expand=directory<br/>
 * Response:
 * <pre>
 * {
 *     "href": "some/account/uri",
 *     "name": "foo",
 *     ...
 *     "directory": {
 *         "href": "the/directory/uri"
 *         "name": "A directory"
 *         "description": "A description",
 *         ...
 *     }
 * }
 * </pre>
 * Notice the expansion directive caused the second result to materialize and include the referenced directory
 * (and its properties) in addition to the other account properties.
 *
 * @since 0.8
 */
public class Expansion implements Criterion {

    private final String name; //name of the link property to expand

    public Expansion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
