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

import com.wandisco.gerrit.client.sshd.commands.GerritCommand;

public final class CreateAccountCommand extends GerritCommand {

    public CreateAccountCommand(String accountName) {
        super("create-account", accountName);
    }

    public void setGroup(String group) {
        addOption(CreateAccountOptions.GROUP, group);
    }

    public void setFullName(String fullName) {
        addDescriptionFlag(CreateAccountOptions.FULL_NAME, fullName);
    }

    public void setEmail(String email) {
        addOption(CreateAccountOptions.EMAIL, email);
    }

    public void setHttpPassword(String httpPassword) {
        addOption(CreateAccountOptions.HTTP_PASSWORD, httpPassword);
    }

    public void setSshKey(String sshKey) {
        addDescriptionFlag(CreateAccountOptions.SSH_KEY, sshKey);
    }

    //BuilderClass
    public static class CreateAccountCommandBuilder {
        private final CreateAccountCommand command;

        public CreateAccountCommandBuilder(String accountName) {
            command = new CreateAccountCommand(accountName);
        }

        public CreateAccountCommandBuilder fullName(String fullName) {
            command.setFullName(fullName);
            return this;
        }

        public CreateAccountCommandBuilder httpPassword(String password) {
            command.setHttpPassword(password);
            return this;
        }

        public CreateAccountCommandBuilder email(String email) {
            command.setEmail(email);
            return this;
        }

        public CreateAccountCommandBuilder group(String group) {
            command.setGroup(group);
            return this;
        }

        public CreateAccountCommandBuilder sshKey(String sshKey) {
            command.setSshKey(sshKey);
            return this;
        }

        public CreateAccountCommand build() {
            return command;
        }
    }
}
