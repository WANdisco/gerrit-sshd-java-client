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

public final class SetProjectCommand extends GerritCommand {

    public SetProjectCommand(String projectName) {
        super("set-project", projectName);
    }

    public void setSubmitType(ProjectSubmitTypes submitType) {
        addOption(SetProjectOptions.SUBMIT_TYPE, submitType.name());
    }

    public void setProjectState(ProjectState state) {
        addOption(SetProjectOptions.PROJECT_STATE, state.name());
    }

    public void setRequireChangeId(ProjectSettingOption option) {
        addOption(SetProjectOptions.CHANGE_ID, option.name());
    }

    public void setMaxObjectSizeLimit(String maxObjectSizeLimit) {
        addOption(SetProjectOptions.MAX_OBJECT_SIZE_LIMIT, maxObjectSizeLimit);
    }

    public void setContentMerge(ProjectSettingOption option) {
        addOption(SetProjectOptions.CONTENT_MERGE, option.name());
    }

    public void setSignedOffBy(ProjectSettingOption option) {
        addOption(SetProjectOptions.SIGNED_OFF_BY, option.name());
    }

    public void setContributorAgreements(ProjectSettingOption option) {
        addOption(SetProjectOptions.CONTRIBUTOR_AGREEMENTS, option.name());
    }

    public void setDescription(String description) {
        addDescriptionFlag(SetProjectOptions.DESCRIPTION, description);
    }

    public enum ProjectSettingOption {
        TRUE,
        FALSE,
        INHERIT
    }


    public enum ProjectState {
        ACTIVE,
        READ_ONLY,
        HIDDEN
    }


    //BuilderClass
    public static class SetProjectCommandBuilder {
        private final SetProjectCommand command;

        public SetProjectCommandBuilder(String projectName) {
            command = new SetProjectCommand(projectName);
        }

        public SetProjectCommandBuilder submitType(ProjectSubmitTypes submitType) {
            command.setSubmitType(submitType);
            return this;
        }

        public SetProjectCommandBuilder changeId(ProjectSettingOption option) {
            command.setRequireChangeId(option);
            return this;
        }

        public SetProjectCommandBuilder maxObjectSizeLimit(String maxObjectSizeLimit) {
            command.setMaxObjectSizeLimit(maxObjectSizeLimit);
            return this;
        }

        public SetProjectCommandBuilder contentMerge(ProjectSettingOption option) {
            command.setContentMerge(option);
            return this;
        }

        public SetProjectCommandBuilder signedOffBy(ProjectSettingOption option) {
            command.setSignedOffBy(option);
            return this;
        }

        public SetProjectCommandBuilder contributorAgreement(ProjectSettingOption option) {
            command.setContributorAgreements(option);
            return this;
        }

        public SetProjectCommandBuilder state(ProjectState state) {
            command.setProjectState(state);
            return this;
        }

        public SetProjectCommandBuilder description(String description) {
            command.setDescription(description);
            return this;
        }

        public SetProjectCommand build() {
            return command;
        }
    }
}
