package com.stormpath.sdk.directory;

import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.organization.Organization;

public class AccountStoreVisitorAdapter implements AccountStoreVisitor {
    @Override
    public void visit(Group group) {
        throw new IllegalStateException("Resolved account store cannot be a Group");
    }

    @Override
    public void visit(Directory directory) {
        throw new IllegalStateException("Resolved account store cannot be a Directory");
    }

    @Override
    public void visit(Organization organization) {
        throw new IllegalStateException("Resolved account store cannot be an Organization");
    }
}
