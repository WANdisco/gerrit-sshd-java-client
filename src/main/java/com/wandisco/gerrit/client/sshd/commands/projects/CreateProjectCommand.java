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

public final class CreateProjectCommand extends GerritCommand {

    public CreateProjectCommand(String projectName) {
        super("create-project", projectName);
    }

    public void setEmptyCommit() {
        addOption(CreateProjectOptions.EMPTY_COMMIT);
    }

    public void setParentName(String parentName) {
        addOption(CreateProjectOptions.PARENT, parentName);
    }

    public void setPermissionsOnly() {
        addOption(CreateProjectOptions.PERMISSIONS_ONLY);
    }

    public void setSubmitType(ProjectSubmitTypes submitType) {
        addOption(CreateProjectOptions.SUBMIT_TYPE, submitType.name());
    }

    public void setOwner(String owner) {
        addOption(CreateProjectOptions.OWNER, owner);
    }

    public void setCreateChangeForAllNotTarget() {
        addOption(CreateProjectOptions.CREATE_NEW_CHANGE_FOR_ALL_NOT_IN_TARGET);
    }

    public void setRequireChangeId() {
        addOption(CreateProjectOptions.REQUIRE_CHANGE_ID);
    }

    public void setMaxObjectSizeLimit(String maxObjectSizeLimit) {
        addOption(CreateProjectOptions.MAX_OBJECT_SIZE_LIMIT, maxObjectSizeLimit);
    }

    public void setUseContentMerge() {
        addOption(CreateProjectOptions.USE_CONTENT_MERGE);
    }

    public void setUseSignedOffBy() {
        addOption(CreateProjectOptions.USE_SIGNED_OFF_BY);
    }

    public void setUseContributorAgreements() {
        addOption(CreateProjectOptions.USE_CONTRIBUTOR_AGREEMENTS);
    }

    public void setDescription(String description) {
        addDescriptionFlag(CreateProjectOptions.DESCRIPTION, description);
    }

    public void setBranch(String branchName) {
        addOption(CreateProjectOptions.BRANCH, branchName);
    }

    //BuilderClass
    public static class CreateProjectCommandBuilder {
        private final CreateProjectCommand command;

        public CreateProjectCommandBuilder(String projectName) {
            command = new CreateProjectCommand(projectName);
        }

        public CreateProjectCommandBuilder emptyCommit() {
            command.setEmptyCommit();
            return this;
        }

        public CreateProjectCommandBuilder parent(String parentName) {
            command.setParentName(parentName);
            return this;
        }

        public CreateProjectCommandBuilder permissionsOnly() {
            command.setPermissionsOnly();
            return this;
        }

        public CreateProjectCommandBuilder submitType(ProjectSubmitTypes submitType) {
            command.setSubmitType(submitType);
            return this;
        }

        public CreateProjectCommandBuilder owner(String owner) {
            command.setOwner(owner);
            return this;
        }

        public CreateProjectCommandBuilder createNewChangeForAllNotInTarget() {
            command.setCreateChangeForAllNotTarget();
            return this;
        }

        public CreateProjectCommandBuilder requireChangeId() {
            command.setRequireChangeId();
            return this;
        }

        public CreateProjectCommandBuilder maxObjectSizeLimit(String maxObjectSizeLimit) {
            command.setMaxObjectSizeLimit(maxObjectSizeLimit);
            return this;
        }

        public CreateProjectCommandBuilder useContentMerge() {
            command.setUseContentMerge();
            return this;
        }

        public CreateProjectCommandBuilder useSignedOffBy() {
            command.setUseSignedOffBy();
            return this;
        }

        public CreateProjectCommandBuilder useContributorAgreement() {
            command.setUseContributorAgreements();
            return this;
        }

        public CreateProjectCommandBuilder description(String description) {
            command.setDescription(description);
            return this;
        }

        public CreateProjectCommandBuilder branch(String branch) {
            command.setBranch(branch);
            return this;
        }

        public CreateProjectCommand build() {
            return command;
        }
    }
}
