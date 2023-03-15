package com.wandisco.gerrit.client.sshd.commands.groups;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wandisco.gerrit.client.sshd.commands.exceptions.GerritCommandException;
import com.wandisco.gerrit.client.sshd.commands.groups.RenameGroupCommand.RenameGroupCommandBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RenameGroupCommandTest {
    private final String renameCommand = "gerrit rename-group";
    private final String groupName = "oldGroup";
    private final String newGroupName = "newGroup";
    private RenameGroupCommand basicCommand;
    private RenameGroupCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new RenameGroupCommand(groupName, newGroupName);
        commandBuilder = new RenameGroupCommandBuilder();
    }

    @Test
    public void testRenameGroupCommand() throws GerritCommandException {
        assertGetMatchesCommand(String.format("%s %s", groupName, newGroupName));
        commandBuilder.group(groupName);
        commandBuilder.newGroupName(newGroupName);
        assertBuilderEqualsCommand();
    }

    @Test
    void testRenameGroupCommandExceptions() {
        GerritCommandException thrown = assertThrows(GerritCommandException.class, () -> new RenameGroupCommandBuilder().build());
        assertEquals("Required Param absent:- GroupSet = false, NewGroupNameSet = false", thrown.getMessage());
    }

    @Test
    void testRenameGroupCommandExceptionGroupOnly() {
        GerritCommandException thrown =
            assertThrows(GerritCommandException.class, () -> new RenameGroupCommandBuilder().group(groupName).build());
        assertEquals("Required Param absent:- GroupSet = true, NewGroupNameSet = false", thrown.getMessage());
    }

    @Test
    void testRenameGroupCommandExceptionNewGroupOnly() {
        GerritCommandException thrown =
            assertThrows(GerritCommandException.class, () -> new RenameGroupCommandBuilder().newGroupName(groupName).build());
        assertEquals("Required Param absent:- GroupSet = false, NewGroupNameSet = true", thrown.getMessage());
    }

    /**
     * Methods under test:
     *
     * <ul>
     *   <li>{@link RenameGroupCommand#RenameGroupCommand(String, String)}
     *   <li>{@link RenameGroupCommand#setGroup(String)}
     *   <li>{@link RenameGroupCommand#setNewGroupName(String)}
     *   <li>{@link RenameGroupCommand#getGroup()}
     *   <li>{@link RenameGroupCommand#getNewGroupName()}
     * </ul>
     */
    @Test
    void testConstructor() {

        String group = "Group";
        String newGroupName = "New Group Name";

        RenameGroupCommand actualRenameGroupCommand = new RenameGroupCommand(group, newGroupName);
        String group1 = "Group";
        actualRenameGroupCommand.setGroup(group1);
        String newGroupName1 = "New Group Name";
        actualRenameGroupCommand.setNewGroupName(newGroupName1);

        assertEquals("Group", actualRenameGroupCommand.getGroup());
        assertEquals("New Group Name", actualRenameGroupCommand.getNewGroupName());
    }

    @Test
    void testConstructor2() {

        String group = "Group";
        String newGroupName = "New Group Name";

        RenameGroupCommand actualRenameGroupCommand = new RenameGroupCommand(group, newGroupName);

        assertEquals("gerrit rename-group Group New Group Name", actualRenameGroupCommand.getCommand());
        assertFalse(actualRenameGroupCommand.isTimedOut());
        assertFalse(actualRenameGroupCommand.isSuccessful());
        assertFalse(actualRenameGroupCommand.isComplete());
        assertTrue(actualRenameGroupCommand.getOutputList().isEmpty());
        assertEquals("New Group Name", actualRenameGroupCommand.getNewGroupName());
        assertEquals("Group", actualRenameGroupCommand.getGroup());
    }

    @Test
    void testGetCommand() {

        RenameGroupCommand renameGroupCommand = new RenameGroupCommand("Group", "New Group Name");

        String actualCommand = renameGroupCommand.getCommand();

        assertEquals("gerrit rename-group Group New Group Name", actualCommand);
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s", renameCommand, flags), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() throws GerritCommandException {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
