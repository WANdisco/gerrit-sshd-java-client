package com.wandisco.gerrit.client.sshd.commands.accounts;

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

public enum SetAccountOptions implements CommandOption {

    FULL_NAME("--full-name"),
    ACTIVE("--active"),
    INACTIVE("--inactive"),
    ADD_EMAIL("--add-email"),
    DELETE_EMAIL("--delete-email"),
    PREFERRED_EMAIL("--preferred-email"),
    ADD_SSH_KEY("--add-ssh-key"),
    DELETE_SSH_KEY("--delete-ssh-key"),
    GENERATE_HTTP_PASSWORD("--generate-http-password"),
    HTTP_PASSWORD("--http-password"),
    CLEAR_HTTP_PASSWORD("--clear-http-password");

    private final String option;

    SetAccountOptions(String option) {
        this.option = option;
    }

    @Override
    public String getFlag() {
        return option;
    }
}
