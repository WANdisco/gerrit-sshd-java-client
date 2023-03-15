package com.wandisco.gerrit.client.sshd.commands.projects;

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

public final class SetHeadCommand extends GerritCommand {
    private static final String SET_HEAD_OPTION = "--new-head";
    private final String project;
    private final String newHeadRef;

    public SetHeadCommand(String project, String newHeadRef) {
        super("set-head");
        this.project = project;
        this.newHeadRef = newHeadRef;
    }

    @Override
    public String getCommand() {
        if (commandBuilder.toString().trim().endsWith(getCommandMethod()) && !getNewHeadRef().isEmpty() && !getProject().isEmpty()) {
            commandBuilder.append(String.format("%s %s %s", getProject(), SET_HEAD_OPTION, getNewHeadRef()));
        }
        return commandBuilder.toString();
    }

    public String getProject() {
        return project;
    }

    public String getNewHeadRef() {
        return newHeadRef;
    }

    //BuilderClass
    public static class SetHeadCommandBuilder {
        private String project;
        private String newHeadRef;
        private boolean projectSet = false;
        private boolean newHeadRefSet = false;

        public SetHeadCommandBuilder() {
        }

        public SetHeadCommandBuilder project(String project) {
            this.project = project;
            projectSet = true;
            return this;
        }

        public SetHeadCommandBuilder newHeadRef(String newHeadRef) {
            this.newHeadRef = newHeadRef;
            newHeadRefSet = true;
            return this;
        }

        public SetHeadCommand build() throws GerritCommandException {
            if (projectSet && newHeadRefSet) {
                return new SetHeadCommand(project, newHeadRef);
            } else {
                throw new GerritCommandException(
                    String.format("Required Param absent:- projectSet = %s, newHeadRefSet = %s", projectSet, newHeadRefSet));
            }
        }

    }
}
