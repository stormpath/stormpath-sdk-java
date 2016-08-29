/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.application;

import com.stormpath.sdk.directory.AccountStore;

/**
 * Interface to be implemented by {@link com.stormpath.sdk.resource.Resource Resources} capable of storing {@link com.stormpath.sdk.directory.AccountStore account stores}. For example:
 * {@link com.stormpath.sdk.application.Application Applications} and {@link com.stormpath.sdk.organization.Organization Organizations}.
 *
 * @since 1.0.RC7
 */
public interface AccountStoreHolder<T extends AccountStoreHolder> {

    /**
     * Returns the {@link AccountStore} (either a {@link com.stormpath.sdk.group.Group}, a
     * {@link com.stormpath.sdk.directory.Directory Directory} or an {@link com.stormpath.sdk.organization.Organization Organization})
     * used to persist new accounts created by the Application or Organization, or {@code null} if no accountStore has been designated.
     * <p/>
     * Because Applications and Organizations are not {@code AccountStore}s, they delegate to an Organization, Group or Directory
     * when creating accounts; this method returns the AccountStore to which the Application or Organization delegate
     * the actual storage of new accounts.
     * <h3>Directory or Group?</h3>
     * As Organization, Group and Directory are sub-interfaces of {@link AccountStore}, you can determine which of the three
     * is returned by using the <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor design pattern</a>.  For
     * example:
     * <p/>
     * <pre>
     * AccountStore accountStore = application.getDefaultAccountStore();
     * accountStore.accept(new {@link com.stormpath.sdk.directory.AccountStoreVisitor AccountStoreVisitor}() {
     *
     *     public void visit(Directory directory) {
     *         //the accountStore is a Directory
     *     }
     *
     *     public void visit(Group group) {
     *         //the accountStore is a Group;
     *     }
     *
     *     public void visit(Organization organization) {
     *         //the accountStore is an Organization;
     *     }
     * };
     * </pre>
     *
     * @return the {@link AccountStore AccountStore} (which will be either a Group, Organization or Directory) used to persist
     *         new accounts created by the Account Store Holder (Application or Organization), or
     *         {@code null} if no accountStore has been designated. Organizations don't actually store Accounts, instead, they delegate
     *         the actual storage to their own internal defaultAccountStore. Therefore, Organizations can be seen as a virtual {@link AccountStore AccountStore}s.
     * @since 0.9
     */
    AccountStore getDefaultAccountStore();

    /**
     * Sets the {@link AccountStore} (either a {@link com.stormpath.sdk.group.Group Group}, {@link com.stormpath.sdk.organization.Organization Organization} or a
     * {@link com.stormpath.sdk.directory.Directory Directory}) used to persist
     * new accounts created by the Application or Organization.
     * <p/>
     * Because Applications and Directories are not {@code AccountStore}s, they delegate to a Group or Directory
     * when creating accounts; this method returns the AccountStore to which the Application or Organization delegate
     * new account persistence.
     * </p>
     *
     * @param accountStore the {@link AccountStore} (which will be either a Group or Directory) used to persist
     *                     new accounts created by the Application or Directory
     */
    void setDefaultAccountStore(AccountStore accountStore);

    /**
     * Returns the {@link AccountStore} used to persist
     * new groups created by the Application or Organization, or
     * {@code null} if no accountStore has been designated. <b>Stormpath's current REST API requires this to be
     * a Directory. However, this could be a Group in the future, so do not assume it is always a
     * Directory if you want your code to be function correctly if/when this support is added.</b>  Avoid casting the
     * returned value directly to a Directory: use the Visitor pattern as explained below.
     * <p/>
     * Because Applications, Organizations and Directories are not {@code AccountStore}s, they delegate to a Group or Directory
     * when creating accounts; this method returns the AccountStore to which the Application or Organization delegate
     * new account persistence.
     * <h3>Organization, Directory or Group?</h3>
     * As Organization, Group and Directory are sub-interfaces of {@link AccountStore}, you can determine which of the three
     * is returned by using the <a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor design pattern</a>.  For
     * example:
     * <p/>
     * <pre>
     * AccountStore groupStore = organization.getDefaultGroupStore();
     * groupStore.accept(new {@link com.stormpath.sdk.directory.AccountStoreVisitor AccountStoreVisitor}() {
     *
     *     public void visit(Directory directory) {
     *         //groupStore is a Directory
     *     }
     *
     *     public void visit(Group group) {
     *         //groupStore is a Group;
     *     }
     *
     *     public void visit(Organization organization) {
     *         //groupStore is an Organization;
     *     }
     * };
     * </pre>
     * Again, in practice, Stormpath's current REST API requires this to be a Directory.  However, this could be
     * a Group in the future, so do not assume it will always be a Directory if you want your code to be
     * forward compatible; use the Visitor pattern and do not cast directly to a Directory.
     *
     * @return the {@link AccountStore} (which will be either a Group or Directory) used to persist
     *         new groups created by the Application or Organization, or
     *         {@code null} if no accountStore has been designated.
     * @since 0.9
     */
    AccountStore getDefaultGroupStore();

    /**
     * Sets the {@link AccountStore} (a {@link com.stormpath.sdk.directory.Directory Directory}) that will be used to
     * persist new groups created by the Application or Organization.
     * <b>Stormpath's current REST API requires this to be
     * a Directory. However, this could be a Group in the future, so do not assume it is always a
     * Directory if you want your code to be function correctly if/when this support is added.</b>
     * <p/>
     * Because Applications and Directories are not {@code AccountStore}s, they delegate to a Group or Directory
     * when creating accounts; this method returns the AccountStore to which the Application or Organization delegate
     * new account persistence.
     *
     * @param accountStore the {@link AccountStore} (which will be either a Group, Organization or Directory) used to persist
     *                     new groups created by the Application or Organization.
     */
    void setDefaultGroupStore(AccountStore accountStore);

}
