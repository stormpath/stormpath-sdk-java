/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.directory;

import com.stormpath.sdk.group.Group;
import com.stormpath.sdk.organization.Organization;

/**
 * An implementation of the {@link AccountStoreVisitor}` interface that just does nothing on each method. It is just
 * available so new visitors do not need to implement all the methods, they can extend this Adapter an override only
 * the specific method they need to.
 */
public class AccountStoreVisitorAdapter implements AccountStoreVisitor {

    @Override
    public void visit(Group group) {
        //no-op
    }

    @Override
    public void visit(Directory directory) {
        //no-op
    }

    @Override
    public void visit(Organization organization) {
        //no-op
    }
}
