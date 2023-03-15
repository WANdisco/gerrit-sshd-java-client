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

public final class CreateGroupCommand extends GerritCommand {

    public CreateGroupCommand(String groupName) {
        super("create-group", groupName);
    }

    public void setOwner(String ownerGroup) {
        addOption(CreateGroupOptions.OWNER, ownerGroup);
    }

    public void setDescription(String description) {
        addDescriptionFlag(CreateGroupOptions.DESCRIPTION, description);
    }

    public void setMember(String member) {
        addOption(CreateGroupOptions.MEMBER, member);
    }

    public void setMemberGroup(String group) {
        addOption(CreateGroupOptions.GROUP, group);
    }

    public void setVisibleToAll() {
        addOption(CreateGroupOptions.VISIBLE_TO_ALL);
    }

    //BuilderClass
    public static class CreateGroupCommandBuilder {
        private final CreateGroupCommand command;

        public CreateGroupCommandBuilder(String groupName) {
            command = new CreateGroupCommand(groupName);
        }

        public CreateGroupCommandBuilder owner(String ownerGroup) {
            command.setOwner(ownerGroup);
            return this;
        }

        public CreateGroupCommandBuilder description(String description) {
            command.setDescription(description);
            return this;
        }

        public CreateGroupCommandBuilder member(String member) {
            command.setMember(member);
            return this;
        }

        public CreateGroupCommandBuilder group(String group) {
            command.setMemberGroup(group);
            return this;
        }

        public CreateGroupCommandBuilder visibleToAll() {
            command.setVisibleToAll();
            return this;
        }

        public CreateGroupCommand build() {
            return command;
        }
    }
}
