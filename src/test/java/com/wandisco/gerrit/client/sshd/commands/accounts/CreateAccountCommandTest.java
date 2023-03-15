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

import com.wandisco.gerrit.client.sshd.commands.accounts.CreateAccountCommand.CreateAccountCommandBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreateAccountCommandTest {

    private final static String twoParamString = "%s %s";
    private final String createCommand = "gerrit create-account";
    private final String userName = "reused";
    private final String fullName = "reused user";
    private final String group = "UserGroup";
    private final String email = "reused@email.com";
    private final String userPassword = "P@ssw0#d";
    private final String userSshKey = "ssh-rsa somekey test@testing";
    private CreateAccountCommand basicCommand;
    private CreateAccountCommandBuilder commandBuilder;

    @BeforeEach
    public void createCommand() {
        basicCommand = new CreateAccountCommand(userName);
        commandBuilder = new CreateAccountCommandBuilder(userName);
    }

    @Test
    public void testBasicConstructor() {
        assertEquals(String.format(twoParamString, createCommand, userName), basicCommand.getCommand());
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetHttpPassword() {
        basicCommand.setHttpPassword(userPassword);
        assertGetMatchesCommand(String.format(twoParamString, CreateAccountOptions.HTTP_PASSWORD.getFlag(), userPassword));
        commandBuilder.httpPassword(userPassword);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetEmail() {
        basicCommand.setEmail(email);
        assertGetMatchesCommand(String.format(twoParamString, CreateAccountOptions.EMAIL.getFlag(), email));
        commandBuilder.email(email);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetFullName() {
        basicCommand.setFullName(fullName);
        assertGetMatchesCommand(String.format(twoParamString, CreateAccountOptions.FULL_NAME.getFlag(), String.format("'%s'", fullName)));
        commandBuilder.fullName(fullName);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetGroup() {
        basicCommand.setGroup(group);
        assertGetMatchesCommand(String.format(twoParamString, CreateAccountOptions.GROUP.getFlag(), group));
        commandBuilder.group(group);
        assertBuilderEqualsCommand();
    }

    @Test
    public void testSetSshKey() {
        basicCommand.setSshKey(userSshKey);
        assertGetMatchesCommand(String.format("%s '%s'", CreateAccountOptions.SSH_KEY.getFlag(), userSshKey));
        commandBuilder.sshKey(userSshKey);
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
        assertEquals(String.format("%s %s %s", createCommand, flags, userName), basicCommand.getCommand());
    }

    private void assertBuilderEqualsCommand() {
        assertEquals(basicCommand.getCommand(), commandBuilder.build().getCommand(), "Builder command did not match constructor command");
    }
}
