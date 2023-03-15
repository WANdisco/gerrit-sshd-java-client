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

public final class SetAccountCommand extends GerritCommand {

    public SetAccountCommand(String user) {
        super("set-account", user);
    }

    public void setFullname(String fullname) {
        addDescriptionFlag(SetAccountOptions.FULL_NAME, fullname);
    }

    public void setActive() {
        addOption(SetAccountOptions.ACTIVE);
    }

    public void setInactive() {
        addOption(SetAccountOptions.INACTIVE);
    }

    public void setAddEmail(String email) {
        addOption(SetAccountOptions.ADD_EMAIL, email);
    }

    public void setDeleteEmail(String email) {
        addOption(SetAccountOptions.DELETE_EMAIL, email);
    }

    public void setPreferredEmail(String email) {
        addOption(SetAccountOptions.PREFERRED_EMAIL, email);
    }

    public void setAddSshKey(String sshKey) {
        addDescriptionFlag(SetAccountOptions.ADD_SSH_KEY, sshKey);
    }

    public void setDeleteSshKey(String sshKey) {
        addDescriptionFlag(SetAccountOptions.DELETE_SSH_KEY, sshKey);
    }

    public void setGenerateHttpPassword() {
        addOption(SetAccountOptions.GENERATE_HTTP_PASSWORD);
    }

    public void setHttpPassword(String password) {
        addOption(SetAccountOptions.HTTP_PASSWORD, password);
    }

    public void setClearHttpPassword() {
        addOption(SetAccountOptions.CLEAR_HTTP_PASSWORD);
    }

    //BuilderClass
    public static class SetAccountCommandBuilder {
        private final SetAccountCommand command;

        public SetAccountCommandBuilder(String accountName) {
            command = new SetAccountCommand(accountName);
        }

        public SetAccountCommandBuilder fullName(String fullName) {
            command.setFullname(fullName);
            return this;
        }

        public SetAccountCommandBuilder setActive() {
            command.setActive();
            return this;
        }

        public SetAccountCommandBuilder setInactive() {
            command.setInactive();
            return this;
        }

        public SetAccountCommandBuilder addEmail(String email) {
            command.setAddEmail(email);
            return this;
        }

        public SetAccountCommandBuilder deleteEmail(String email) {
            command.setDeleteEmail(email);
            return this;
        }

        public SetAccountCommandBuilder preferredEmail(String email) {
            command.setPreferredEmail(email);
            return this;
        }

        public SetAccountCommandBuilder addSshKey(String sshKey) {
            command.setAddSshKey(sshKey);
            return this;
        }

        public SetAccountCommandBuilder deleteSshKey(String sshKey) {
            command.setDeleteSshKey(sshKey);
            return this;
        }

        public SetAccountCommandBuilder generateHttpPass() {
            command.setGenerateHttpPassword();
            return this;
        }

        public SetAccountCommandBuilder httpPassword(String password) {
            command.setHttpPassword(password);
            return this;
        }

        public SetAccountCommandBuilder clearHttpPassword() {
            command.setClearHttpPassword();
            return this;
        }

        public SetAccountCommand build() {
            return command;
        }
    }
}
