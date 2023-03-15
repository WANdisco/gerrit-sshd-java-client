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
import com.wandisco.gerrit.client.sshd.commands.exceptions.GerritCommandException;

public final class RenameGroupCommand extends GerritCommand {

    private String group;
    private String newGroupName;

    public RenameGroupCommand(String group, String newGroupName) {
        super("rename-group");
        this.group = group;
        this.newGroupName = newGroupName;
    }

    @Override
    public String getCommand() {
        if (commandBuilder.toString().trim().endsWith(getCommandMethod())) {
            commandBuilder.append(String.format("%s %s", getGroup(), getNewGroupName()));
        }
        return commandBuilder.toString();
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getNewGroupName() {
        return newGroupName;
    }

    public void setNewGroupName(String newGroupName) {
        this.newGroupName = newGroupName;
    }

    //BuilderClass
    public static class RenameGroupCommandBuilder {
        private String group;
        private String newGroupName;
        private boolean groupSet = false;
        private boolean newGroupNameSet = false;

        public RenameGroupCommandBuilder() {
        }

        public RenameGroupCommandBuilder group(String group) {
            this.group = group;
            groupSet = true;
            return this;
        }

        public RenameGroupCommandBuilder newGroupName(String newGroupName) {
            this.newGroupName = newGroupName;
            newGroupNameSet = true;
            return this;
        }

        public RenameGroupCommand build() throws GerritCommandException {
            if (groupSet && newGroupNameSet) {
                return new RenameGroupCommand(group, newGroupName);
            } else {
                throw new GerritCommandException(
                    String.format("Required Param absent:- GroupSet = %s, NewGroupNameSet = %s", groupSet, newGroupNameSet));
            }
        }

    }
}
