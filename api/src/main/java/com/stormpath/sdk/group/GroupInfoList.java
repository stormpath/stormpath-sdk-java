package com.stormpath.sdk.group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzumbado on 4/22/16.
 */
public class GroupInfoList {
    private List<GroupInfo> groupList;

    public GroupInfoList() {
        groupList = new ArrayList<GroupInfo>();
    }

    public List<GroupInfo> getGroupList() {
        return groupList;
    }

    public void setGroupList(GroupList groupList) {
        this.groupList.clear();
        for (Group group : groupList){
            this.groupList.add(new GroupInfo(group));
        }
    }
}
