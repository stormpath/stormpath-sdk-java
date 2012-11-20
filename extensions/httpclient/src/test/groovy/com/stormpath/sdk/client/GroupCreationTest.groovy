package com.stormpath.sdk.client

import com.stormpath.sdk.directory.Directory
import com.stormpath.sdk.group.Group

/**
 * Created with IntelliJ IDEA.
 * User: ecrisostomo
 * Date: 11/19/12
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates.
 */
class GroupCreationTest {

    public static void main(String[] args) {

        DefaultApiKey apiKey = new DefaultApiKey(args[0], args[1]);

        Client client = new Client(apiKey, args[2]);

        String directoryHref = args[3];

        Directory directory = client.getDataStore().getResource(directoryHref, Directory.class);

        Group group = client.getDataStore().instantiate(Group.class);

        group.setDescription("New Group Desc");
        group.setName("New Group");

        directory.createGroup(group);

        if (group.getHref() != null && !group.getHref().isEmpty()) {
            println("Group Created!") ;
        } else {
            throw new Exception("Group was not created!");
        }
    }
}
