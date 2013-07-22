package com.stormpath.sdk.directory;

import com.stormpath.sdk.group.Group;

/**
 * @since 0.9
 */
public interface AccountStoreVisitor {

    void visit(Group group);

    void visit(Directory directory);
}
