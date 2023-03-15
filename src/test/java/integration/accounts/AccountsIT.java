package integration.accounts;

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

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.wandisco.gerrit.client.sshd.GerritSSHClient;
import com.wandisco.gerrit.client.sshd.commands.accounts.CreateAccountCommand;
import com.wandisco.gerrit.client.sshd.commands.accounts.CreateAccountCommand.CreateAccountCommandBuilder;
import com.wandisco.gerrit.client.sshd.model.task.GerritBaseCommand;
import integration.utils.TestBase;
import java.io.IOException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;


public class AccountsIT extends TestBase {

    @Test
    public void testUserCreateByCommand() throws IOException, RestApiException {
        String user = RandomStringUtils.randomAlphanumeric(10);
        String userGroup = "Administrators";
        String userEmail = String.format("%s@email.com", user);
        String userPassword = RandomStringUtils.randomAlphanumeric(10);
        String userFullName = String.format("%s %s",
                RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));

        CreateAccountCommand createAccountCommand = new CreateAccountCommand(user);
        createAccountCommand.setGroup(userGroup);
        createAccountCommand.setEmail(userEmail);
        createAccountCommand.setFullName(userFullName);
        createAccountCommand.setHttpPassword(userPassword);
        createAccountCommand.setSshKey(getPublicKey());
        testUserCommand(createAccountCommand, user, userPassword);
    }

    @Test
    public void testUserCreateByBuilder() throws RestApiException, IOException {
        String user = RandomStringUtils.randomAlphanumeric(10);
        String userGroup = "Administrators";
        String userEmail = String.format("%s@email.com", user);
        String userPassword = RandomStringUtils.randomAlphanumeric(10);
        String userFullName = String.format("%s %s",
                RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5));

        CreateAccountCommandBuilder commandBuilder =
            new CreateAccountCommandBuilder(user).group(userGroup).email(userEmail).httpPassword(userPassword).sshKey(getPublicKey())
                .fullName(userFullName);
        testUserCommand(commandBuilder.build(), user, userPassword);
    }

    private void testUserCommand(CreateAccountCommand command, String username, String password) throws RestApiException, IOException {
        GerritBaseCommand output = getAdminGerritSSHClient().executeCommand(command);
        assertEquals(0, output.getExitCode());
        GerritApi apiClient = getNewGerritApiClient(username, password);
        apiClient.accounts().self().get();
        GerritSSHClient userClient = getNewGerritSSHClient(getNewGerritSSHServer(username));
        output = userClient.executeCommand("gerrit ls-projects");
        assertEquals(0, output.getExitCode());
    }
}
