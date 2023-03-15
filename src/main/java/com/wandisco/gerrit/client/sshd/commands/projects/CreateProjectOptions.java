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

import com.wandisco.gerrit.client.sshd.model.task.CommandOption;

public enum CreateProjectOptions implements CommandOption {

    BRANCH("--branch"),
    CREATE_NEW_CHANGE_FOR_ALL_NOT_IN_TARGET("--create-new-change-for-all-not-in-target"),
    DESCRIPTION("--description"),
    EMPTY_COMMIT("--empty-commit"),
    MAX_OBJECT_SIZE_LIMIT("--max-object-size-limit"),
    OWNER("--owner"),
    PARENT("--parent"),
    PERMISSIONS_ONLY("--permissions-only"),
    PLUGIN_CONFIG("--plugin-config"),
    REQUIRE_CHANGE_ID("--require-change-id"),
    SUBMIT_TYPE("--submit-type"),
    SUGGEST_PARENT("--suggest-parents"),
    USE_CONTENT_MERGE("--use-content-merge"),
    USE_CONTRIBUTOR_AGREEMENTS("--use-contributor-agreements"),
    USE_SIGNED_OFF_BY("--use-signed-off-by");

    private final String flag;

    CreateProjectOptions(String option) {
        this.flag = option;
    }

    @Override
    public String getFlag() {
        return flag;
    }

}
