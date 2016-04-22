package com.stormpath.sdk.group;

/**
 * Created by mzumbado on 4/21/16.
 */
public class GroupInfo {
    String name;
    String description;
    String status;
    String tenant;
    String directory;

    public GroupInfo(Group group) {
        this.name = group.getName();
        this.description = group.getDescription();
        this.status = group.getStatus() != null? group.getStatus().name(): "";
        this.tenant = group.getTenant() != null? group.getTenant().getName(): "";
        this.directory = group.getDirectory() != null? group.getDirectory().getName(): "";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getTenant() {
        return tenant;
    }

    public String getDirectory() {
        return directory;
    }
}
