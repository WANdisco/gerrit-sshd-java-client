package com.wandisco.gerrit.client.sshd.commands.accounts;

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

import com.wandisco.gerrit.client.sshd.commands.accounts.SetAccountCommand.SetAccountCommandBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SetAccountCommandTest {

    private final static String twoParamString = "%s %s";
    private final String setCommand = "gerrit set-account";
    private final String userName = "reused";
    private final String fullName = "reused user";
    private final String group = "UserGroup";
    private final String email = "reused@email.com";
    private final String userPassword = "P@ssw0#d";
    private final String userSshKey = "ssh-rsa somekey test@testing";
    private SetAccountCommand basicCommand;
    private SetAccountCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new SetAccountCommand(userName);
        commandBuilder = new SetAccountCommandBuilder(userName);
    }

    @Test
    public void testBasicConstructor() {
        assertEquals(String.format(twoParamString, setCommand, userName), basicCommand.getCommand());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetFullName() {
        basicCommand.setFullname(fullName);
        assertGetMatchesCommand(String.format("%s '%s'", SetAccountOptions.FULL_NAME.getFlag(), fullName));
        commandBuilder.fullName(fullName);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetActive() {
        basicCommand.setActive();
        assertGetMatchesCommand(SetAccountOptions.ACTIVE.getFlag());
        commandBuilder.setActive();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetInactive() {
        basicCommand.setInactive();
        assertGetMatchesCommand(SetAccountOptions.INACTIVE.getFlag());
        commandBuilder.setInactive();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetAddEmail() {
        basicCommand.setAddEmail(email);
        assertGetMatchesCommand(String.format(twoParamString, SetAccountOptions.ADD_EMAIL.getFlag(), email));
        commandBuilder.addEmail(email);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetDeleteEmail() {
        basicCommand.setDeleteEmail(email);
        assertGetMatchesCommand(String.format(twoParamString, SetAccountOptions.DELETE_EMAIL.getFlag(), email));
        commandBuilder.deleteEmail(email);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetPreferredEmail() {
        basicCommand.setPreferredEmail(email);
        assertGetMatchesCommand(String.format(twoParamString, SetAccountOptions.PREFERRED_EMAIL.getFlag(), email));
        commandBuilder.preferredEmail(email);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetAddSshKey() {
        basicCommand.setAddSshKey(userSshKey);
        assertGetMatchesCommand(String.format("%s '%s'", SetAccountOptions.ADD_SSH_KEY.getFlag(), userSshKey));
        commandBuilder.addSshKey(userSshKey);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetDeleteSshKey() {
        basicCommand.setDeleteSshKey(userSshKey);
        assertGetMatchesCommand(String.format("%s '%s'", SetAccountOptions.DELETE_SSH_KEY.getFlag(), userSshKey));
        commandBuilder.deleteSshKey(userSshKey);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetGenerateHttpPassword() {
        basicCommand.setGenerateHttpPassword();
        assertGetMatchesCommand(SetAccountOptions.GENERATE_HTTP_PASSWORD.getFlag());
        commandBuilder.generateHttpPass();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetHttpPassword() {
        basicCommand.setHttpPassword(userPassword);
        assertGetMatchesCommand(String.format(twoParamString, SetAccountOptions.HTTP_PASSWORD.getFlag(), userPassword));
        commandBuilder.httpPassword(userPassword);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetClearHttpPassword() {
        basicCommand.setClearHttpPassword();
        assertGetMatchesCommand(SetAccountOptions.CLEAR_HTTP_PASSWORD.getFlag());
        commandBuilder.clearHttpPassword();
        assertBuilderEqualsCommand();
    }

    @Test
    public void testMultipleGetCommands() {
        String firstCommand = basicCommand.getCommand();
        for (int i = 0; i < 10; i++) {
            assertEquals(firstCommand, basicCommand.getCommand());
        }
    }

    private void assertGetMatchesCommand(String flags) {
        assertEquals(String.format("%s %s %s", setCommand, flags, userName), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
