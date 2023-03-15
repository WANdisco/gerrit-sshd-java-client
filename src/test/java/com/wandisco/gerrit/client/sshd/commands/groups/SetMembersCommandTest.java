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

import com.wandisco.gerrit.client.sshd.commands.groups.SetMembersCommand.SetMembersCommandBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SetMembersCommandTest {

    private final static String twoParamString = "%s %s";
    private final String setCommand = "gerrit set-members";
    private final String groupName = "reusedGroup";
    private final String membershipGroup = "UserGroup";
    private final String member = "reused.member";
    private SetMembersCommand basicCommand;
    private SetMembersCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new SetMembersCommand(groupName);
        commandBuilder = new SetMembersCommandBuilder(groupName);
    }

    @Test
    public void testBasicConstructor() {
        assertEquals(String.format(twoParamString, setCommand, groupName), basicCommand.getCommand());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetAdd() {
        basicCommand.setAdd(member);
        assertGetMatchesCommand(String.format(twoParamString, SetMembersOptions.ADD.getFlag(), member));
        commandBuilder.add(member);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetRemove() {
        basicCommand.setRemove(member);
        assertGetMatchesCommand(String.format(twoParamString, SetMembersOptions.REMOVE.getFlag(), member));
        commandBuilder.remove(member);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetInclude() {
        basicCommand.setInclude(membershipGroup);
        assertGetMatchesCommand(String.format(twoParamString, SetMembersOptions.INCLUDE.getFlag(), membershipGroup));
        commandBuilder.include(membershipGroup);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetExclude() {
        basicCommand.setExclude(membershipGroup);
        assertGetMatchesCommand(String.format(twoParamString, SetMembersOptions.EXCLUDE.getFlag(), membershipGroup));
        commandBuilder.exclude(membershipGroup);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testRepeatedGetCommand() {
        String startingCommand = basicCommand.getCommand();
        for (int i = 0; i < 10; i++) {
            assertEquals(startingCommand, basicCommand.getCommand());
        }
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s %s", setCommand, flags.trim(), groupName), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
