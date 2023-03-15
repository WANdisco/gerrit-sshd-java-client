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

import com.wandisco.gerrit.client.sshd.commands.GerritCommand;

public class LsGroupsCommand extends GerritCommand {

    public LsGroupsCommand() {
        super("ls-groups");
    }

    @Override
    public String getCommand() {
        return commandBuilder.toString().trim();
    }

    public void setProject(String project) {
        addOption(LsGroupsOptions.PROJECT, project);
    }

    public void setUser(String username) {
        addOption(LsGroupsOptions.USER, username);
    }

    public void setOwned() {
        addOption(LsGroupsOptions.OWNED);
    }

    public void setQueryFlag() {
        addOption(LsGroupsOptions.QUERY);
    }

    public void setVisibleToAll() {
        addOption(LsGroupsOptions.VISIBLE_TO_ALL);
    }

    public void setVerbose() {
        addOption(LsGroupsOptions.VERBOSE);
    }

    //BuilderClass
    public static class LSGroupSCommandBuilder {
        private final LsGroupsCommand command;

        public LSGroupSCommandBuilder() {
            command = new LsGroupsCommand();
        }

        public LsGroupsCommand.LSGroupSCommandBuilder project(String project) {
            command.setProject(project);
            return this;
        }

        public LsGroupsCommand.LSGroupSCommandBuilder user(String username) {
            command.setUser(username);
            return this;
        }

        public LsGroupsCommand.LSGroupSCommandBuilder owned() {
            command.setOwned();
            return this;
        }

        public LsGroupsCommand.LSGroupSCommandBuilder visibleToAll() {
            command.setVisibleToAll();
            return this;
        }

        public LsGroupsCommand.LSGroupSCommandBuilder addQuery() {
            command.setQueryFlag();
            return this;
        }

        public LsGroupsCommand.LSGroupSCommandBuilder verbose() {
            command.setVerbose();
            return this;
        }

        public LsGroupsCommand build() {
            return command;
        }
    }
}
