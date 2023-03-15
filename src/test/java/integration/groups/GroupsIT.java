package integration.groups;

/*-
 * #%L
 * gerrit-sshd-java-client
 * %%
 * Copyright (C) 2021 - 2023 WANdisco
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gerrit.extensions.common.GroupInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.wandisco.gerrit.client.sshd.commands.groups.CreateGroupCommand;
import com.wandisco.gerrit.client.sshd.commands.groups.LsGroupsCommand;
import integration.utils.TestBase;
import java.io.IOException;
import org.junit.jupiter.api.Test;



public class GroupsIT extends TestBase {

    //Blocked until 0.9.4 of api client
    @Test
    public void testCreateGroup() throws IOException, RestApiException {
        String groupname = "group1";
        String groupOwner = "Administrators";
        String groupMember = "Administrators";
        String groupDesc = "A Brand new Group";
        CreateGroupCommand createGroupCommand = new CreateGroupCommand(groupname);
        createGroupCommand.setVisibleToAll();
        createGroupCommand.setOwner(groupOwner);
        createGroupCommand.setMemberGroup(groupMember);
        createGroupCommand.setDescription(groupDesc);
        createGroupCommand = (CreateGroupCommand) getAdminGerritSSHClient().executeCommand(createGroupCommand);
        assertEquals(0, createGroupCommand.getExitCode());
        boolean found = false;
        GroupInfo groupInfo = new GroupInfo();
        for (GroupInfo info : getAdminGerritApiClient().groups().list().getAsMap().values()) {
            if (info.name.equals(groupname)) {
                found = true;
                groupInfo = info;
                break;
            }
        }
        assertTrue(found);
        assertEquals(groupInfo.description, groupDesc);
        assertEquals(groupInfo.owner, groupOwner);
        assertTrue(groupInfo.options.visibleToAll);
    }

    @Test
    public void testLsGroups() throws IOException {
        LsGroupsCommand lsGroupsCommand = new LsGroupsCommand();
        lsGroupsCommand.setUser("admin");
        lsGroupsCommand = (LsGroupsCommand) getAdminGerritSSHClient().executeCommand(lsGroupsCommand);
        assertEquals(0,lsGroupsCommand.getExitCode(),lsGroupsCommand.getOutput());
        System.out.println(lsGroupsCommand.getOutput());
        System.out.println(lsGroupsCommand.getOutputList().size());
    }
}
