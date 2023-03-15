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

public enum SetProjectOptions implements CommandOption {

    DESCRIPTION("--description"),
    SUBMIT_TYPE("--submit-type"),
    CONTRIBUTOR_AGREEMENTS("--contributor-agreements"),
    SIGNED_OFF_BY("--signed-off-by"),
    CONTENT_MERGE("--content-merge"),
    CHANGE_ID("--change-id"),
    PROJECT_STATE("--project-state"),
    MAX_OBJECT_SIZE_LIMIT("--max-object-size-limit"),
    ;

    private final String flag;

    SetProjectOptions(String option) {
        this.flag = option;
    }

    @Override
    public String getFlag() {
        return flag;
    }

}
