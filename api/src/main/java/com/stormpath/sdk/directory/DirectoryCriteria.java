package com.stormpath.sdk.directory;

import com.stormpath.sdk.query.Criteria;

/**
 * @since 0.8
 */
public interface DirectoryCriteria extends Criteria<DirectoryCriteria>, DirectoryOptions<DirectoryCriteria> {

    DirectoryCriteria orderByName();

    DirectoryCriteria orderByDescription();

    DirectoryCriteria orderByStatus();
}
