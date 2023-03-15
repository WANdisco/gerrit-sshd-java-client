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

public final class SetMembersCommand extends GerritCommand {

    public SetMembersCommand(String group) {
        super("set-members", group);
    }

    public void setAdd(String member) {
        addOption(SetMembersOptions.ADD, member);
    }

    public void setRemove(String member) {
        addOption(SetMembersOptions.REMOVE, member);
    }

    public void setInclude(String group) {
        addOption(SetMembersOptions.INCLUDE, group);
    }

    public void setExclude(String group) {
        addOption(SetMembersOptions.EXCLUDE, group);
    }

    //BuilderClass
    public static class SetMembersCommandBuilder {
        private final SetMembersCommand command;

        public SetMembersCommandBuilder(String groupName) {
            command = new SetMembersCommand(groupName);
        }

        public SetMembersCommandBuilder add(String member) {
            command.setAdd(member);
            return this;
        }

        public SetMembersCommandBuilder remove(String member) {
            command.setRemove(member);
            return this;
        }

        public SetMembersCommandBuilder include(String group) {
            command.setInclude(group);
            return this;
        }

        public SetMembersCommandBuilder exclude(String group) {
            command.setExclude(group);
            return this;
        }

        public SetMembersCommand build() {
            return command;
        }
    }
}
