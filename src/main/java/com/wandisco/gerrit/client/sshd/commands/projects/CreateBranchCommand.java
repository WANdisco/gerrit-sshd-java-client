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

public final class CreateBranchCommand extends GerritCommand {

    private String project;
    private String branchName;
    private String revision;

    public CreateBranchCommand(String project, String branchName, String revision) {
        super("create-branch");
        this.project = project;
        this.branchName = branchName;
        this.revision = revision;
    }

    @Override
    public String getCommand() {
        if (commandBuilder.toString().trim().endsWith(getCommandMethod())) {
            commandBuilder.append(String.format("%s %s %s", getProject(), getBranchName(), getRevision()));
        }
        return commandBuilder.toString();
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    //BuilderClass
    public static class CreateBranchCommandBuilder {
        private String projectName;
        private String branchName;
        private String revision;
        private boolean projectSet = false;
        private boolean branchSet = false;
        private boolean revisionSet = false;

        public CreateBranchCommandBuilder() {
        }

        public CreateBranchCommandBuilder project(String project) {
            this.projectName = project;
            projectSet = true;
            return this;
        }

        public CreateBranchCommandBuilder branch(String branchName) {
            this.branchName = branchName;
            branchSet = true;
            return this;
        }

        public CreateBranchCommandBuilder revision(String revision) {
            this.revision = revision;
            revisionSet = true;
            return this;
        }

        public CreateBranchCommand build() throws GerritCommandException {
            if (projectSet && branchSet && revisionSet) {
                return new CreateBranchCommand(projectName, branchName, revision);
            } else {
                throw new GerritCommandException(
                    String.format("Required Param absent:- ProjectSet = %s, BranchSet = %s, RevisionSet = %s", projectSet, branchSet,
                        revisionSet));
            }
        }

    }
}
