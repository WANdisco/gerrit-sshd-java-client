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

import com.wandisco.gerrit.client.sshd.commands.groups.CreateGroupCommand.CreateGroupCommandBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateGroupCommandTest {

    private final static String twoParamString = "%s %s";
    private final String createCommand = "gerrit create-group";
    private final String groupName = "reusedGroup";
    private final String groupOwner = "reused.owner";
    private final String membershipGroup = "UserGroup";
    private final String member = "reused.member";
    private final String groupDescription = "a reused group description";
    private CreateGroupCommand basicCommand;
    private CreateGroupCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new CreateGroupCommand(groupName);
        commandBuilder = new CreateGroupCommandBuilder(groupName);
    }

    @Test
    public void testBasicConstructor() {
        assertEquals(String.format(twoParamString, createCommand, groupName), basicCommand.getCommand());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetOwner() {
        basicCommand.setOwner(groupOwner);
        assertGetMatchesCommand(String.format("%s %s", CreateGroupOptions.OWNER.getFlag(), groupOwner));
        commandBuilder.owner(groupOwner);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetDescription() {
        basicCommand.setDescription(groupDescription);
        assertGetMatchesCommand(String.format("%s '%s'", CreateGroupOptions.DESCRIPTION.getFlag(), groupDescription));
        commandBuilder.description(groupDescription);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetMember() {
        basicCommand.setMember(member);
        assertGetMatchesCommand(String.format(twoParamString, CreateGroupOptions.MEMBER.getFlag(), member));
        commandBuilder.member(member);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetMemberGroup() {
        basicCommand.setMemberGroup(membershipGroup);
        assertGetMatchesCommand(String.format(twoParamString, CreateGroupOptions.GROUP.getFlag(), membershipGroup));
        commandBuilder.group(membershipGroup);
        assertBuilderEqualsCommand();
    }

    @Test
    public void setVisibleToAll() {
        basicCommand.setVisibleToAll();
        assertGetMatchesCommand(CreateGroupOptions.VISIBLE_TO_ALL.getFlag());
        commandBuilder.visibleToAll();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testMultipleGetCommands() {
        String firstCommand = basicCommand.getCommand();
        for (int i = 0; i < 10; i++) {
            assertEquals(firstCommand, basicCommand.getCommand());
        }
    }

    @Test
    public void testAddMultipleMembers() {
        StringBuilder expected = new StringBuilder();
        basicCommand.setMember(member);
        commandBuilder.member(member);
        expected.append(String.format("%s %s ", CreateGroupOptions.MEMBER.getFlag(), member));
        for (int i = 0; i < 3; i++) {
            basicCommand.setMember(member + i);
            commandBuilder.member(member + i);
            expected.append(String.format("%s %s%s ", CreateGroupOptions.MEMBER.getFlag(), member, i));
        }
        assertGetMatchesCommand(expected.toString());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testAddMultipleGroups() {
        StringBuilder expected = new StringBuilder();
        basicCommand.setMemberGroup(membershipGroup);
        commandBuilder.group(membershipGroup);
        expected.append(String.format("%s %s ", CreateGroupOptions.GROUP.getFlag(), membershipGroup));
        for (int i = 0; i < 3; i++) {
            basicCommand.setMemberGroup(membershipGroup + i);
            commandBuilder.group(membershipGroup + i);
            expected.append(String.format("%s %s%s ", CreateGroupOptions.GROUP.getFlag(), membershipGroup, i));
        }
        assertGetMatchesCommand(expected.toString());
        assertBuilderEqualsCommand();
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s %s", createCommand, flags.trim(), groupName), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
