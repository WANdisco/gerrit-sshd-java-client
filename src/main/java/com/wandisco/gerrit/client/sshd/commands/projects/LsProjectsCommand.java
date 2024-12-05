package com.wandisco.gerrit.client.sshd.commands.projects;

/*-
 * #%L
 * gerrit-sshd-java-client
 * %%
 * Copyright (C) 2021 - 2024 WANdisco
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

public class LsProjectsCommand extends GerritCommand {
    public LsProjectsCommand() {
        super("ls-projects");
    }

    public void setBranch(String branch) {
        this.addOption(LsProjectsOptions.BRANCH, branch);
    }

    public void setDescription() {
        this.addOption(LsProjectsOptions.DESCRIPTION);
    }

    public void setTree() {
        this.addOption(LsProjectsOptions.TREE);
    }

    public void setMatch(String matchProject) {
        this.addOption(LsProjectsOptions.MATCH, matchProject);
    }

    public void setRegex(String matchProjectRegex) {
        this.addOption(LsProjectsOptions.REGEX, matchProjectRegex);
    }

    public void setStart(String numberOfProjectsToSkip) {
        this.addOption(LsProjectsOptions.START, numberOfProjectsToSkip);
    }

    public void setState(SetProjectCommand.ProjectState projectState) {
        this.addOption(LsProjectsOptions.STATE, projectState.toString());
    }

    public void setType(LsProjectsOptions.Type type) {
        this.addOption(LsProjectsOptions.TYPE, type.toString());
    }

    public void setFormat(LsProjectsOptions.Format format) {
        this.addOption(LsProjectsOptions.FORMAT, format.toString());
    }

    public void setAll() {
        this.addOption(LsProjectsOptions.ALL);
    }

    public void setLimit(String limit) {
        this.addOption(LsProjectsOptions.LIMIT, limit);
    }

    public void setPrefix(String prefix) {
        this.addOption(LsProjectsOptions.PREFIX, prefix);
    }

    public void setHasACLFor(String groupACL) {
        this.addOption(LsProjectsOptions.HAS_ACL_FOR, groupACL);
    }

    public String getCommand() {
        return this.commandBuilder.toString().trim();
    }

    public static class LSProjectsCommandBuilder {
        private final LsProjectsCommand command = new LsProjectsCommand();

        public LSProjectsCommandBuilder() {
        }

        public LsProjectsCommand build() {
            return this.command;
        }

        public LSProjectsCommandBuilder branch(String branch) {
            this.command.setBranch(branch);
            return this;
        }

        public LSProjectsCommandBuilder description() {
            this.command.setDescription();
            return this;
        }

        public LSProjectsCommandBuilder tree() {
            this.command.setTree();
            return this;
        }

        public LSProjectsCommandBuilder match(String match) {
            this.command.setMatch(match);
            return this;
        }

        public LSProjectsCommandBuilder regex(String regex) {
            this.command.setRegex(regex);
            return this;
        }

        public LSProjectsCommandBuilder start(String start) {
            this.command.setStart(start);
            return this;
        }

        public LSProjectsCommandBuilder limit(String limit) {
            this.command.setLimit(limit);
            return this;
        }

        public LSProjectsCommandBuilder state(SetProjectCommand.ProjectState projectState) {
            this.command.setState(projectState);
            return this;
        }

        public LSProjectsCommandBuilder type(LsProjectsOptions.Type type) {
            this.command.setType(type);
            return this;
        }

        public LSProjectsCommandBuilder format(LsProjectsOptions.Format format) {
            this.command.setFormat(format);
            return this;
        }

        public LSProjectsCommandBuilder all() {
            this.command.setAll();
            return this;
        }

        public LSProjectsCommandBuilder prefix(String prefix) {
            this.command.setPrefix(prefix);
            return this;
        }

        public LSProjectsCommandBuilder hasACLFor(String hasACLForGroup) {
            this.command.setHasACLFor(hasACLForGroup);
            return this;
        }
    }
}
