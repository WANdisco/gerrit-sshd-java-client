package com.wandisco.gerrit.client.sshd.commands.changes;

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

public final class ReviewCommand extends GerritCommand {

    public ReviewCommand(String review) {
        super("review", review);
    }

    public void setProject(String project) {
        addOption(ReviewOptions.PROJECT, project);
    }

    public void setBranch(String branch) {
        addOption(ReviewOptions.BRANCH, branch);
    }

    public void setMessage(String message) {
        addDescriptionFlag(ReviewOptions.MESSAGE, message);
    }

    public void setNotify(ReviewNotifyTypes notifyType) {
        addOption(ReviewOptions.NOTIFY, notifyType.name());
    }

    public void setSubmit() {
        addOption(ReviewOptions.SUBMIT);
    }

    public void setAbandon() {
        addOption(ReviewOptions.ABANDON);
    }

    public void setRestore() {
        addOption(ReviewOptions.RESTORE);
    }

    public void setRebase() {
        addOption(ReviewOptions.REBASE);
    }

    public void setMove(String newBranch) {
        addOption(ReviewOptions.MOVE, newBranch);
    }

    public void setJson(String jsonToString) {
        addDescriptionFlag(ReviewOptions.JSON, jsonToString);
    }

    public void setVerified(String score) {
        addOption(ReviewOptions.VERIFIED, score);
    }

    public void setCodeReview(String score) {
        addOption(ReviewOptions.CODE_REVIEW, score);
    }

    public void setCustomLabel(String label, String score) {
        addOption(ReviewOptions.LABEL, String.format("%s=%s", label, score));
    }

    public void setTag(String tag) {
        addOption(ReviewOptions.TAG, tag);
    }

    //BuilderClass
    public static class ReviewCommandBuilder {
        private final ReviewCommand command;

        public ReviewCommandBuilder(String review) {
            command = new ReviewCommand(review);
        }

        public ReviewCommandBuilder project(String project) {
            command.setProject(project);
            return this;
        }

        public ReviewCommandBuilder branch(String branch) {
            command.setBranch(branch);
            return this;
        }

        public ReviewCommandBuilder message(String message) {
            command.setMessage(message);
            return this;
        }

        public ReviewCommandBuilder notifyHandler(ReviewNotifyTypes notifyType) {
            command.setNotify(notifyType);
            return this;
        }

        public ReviewCommandBuilder submit() {
            command.setSubmit();
            return this;
        }

        public ReviewCommandBuilder abandon() {
            command.setAbandon();
            return this;
        }

        public ReviewCommandBuilder restore() {
            command.setRestore();
            return this;
        }

        public ReviewCommandBuilder rebase() {
            command.setRebase();
            return this;
        }

        public ReviewCommandBuilder move(String newBranch) {
            command.setMove(newBranch);
            return this;
        }

        public ReviewCommandBuilder json(String jsonToString) {
            command.setJson(jsonToString);
            return this;
        }

        public ReviewCommandBuilder verify(String score) {
            command.setVerified(score);
            return this;
        }

        public ReviewCommandBuilder codeReview(String score) {
            command.setCodeReview(score);
            return this;
        }

        public ReviewCommandBuilder label(String label, String score) {
            command.setCustomLabel(label, score);
            return this;
        }

        public ReviewCommandBuilder tag(String tag) {
            command.setTag(tag);
            return this;
        }

        public ReviewCommand build() {
            return command;
        }
    }
}
